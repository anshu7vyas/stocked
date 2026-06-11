package io.github.anshu7vyas.stocked.ui.home

import app.cash.turbine.test
import io.github.anshu7vyas.stocked.data.Product
import io.github.anshu7vyas.stocked.data.ProductDao
import io.github.anshu7vyas.stocked.data.ProductRepository
import java.time.LocalDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var fakeDao: FakeProductDao

    // Constructed per-test AFTER seeding: the init-block sweep runs on creation, and a
    // second instance from setUp would run a concurrent sweep against the same fake.
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        fakeDao = FakeProductDao()
    }

    private fun createViewModel(): HomeViewModel =
        HomeViewModel(ProductRepository(fakeDao), CoroutineScope(SupervisorJob() + dispatcher))

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `uiState emits stocked items with days left`() = runTest {
        val expiry = LocalDate.now().plusDays(5)
        fakeDao.seed(
            Product(id = 1, name = "Milk", category = "Dairy products",
                expiryEpochDay = expiry.toEpochDay(), stocked = true)
        )
        viewModel = createViewModel()

        viewModel.uiState.test {
            awaitItem() // initial empty state
            val state = awaitItem()
            assertEquals(1, state.items.size)
            assertEquals("Milk", state.items.first().product.name)
            assertEquals(5, state.items.first().daysLeft)
        }
    }

    @Test
    fun `init sweeps overdue items to expired`() = runTest {
        fakeDao.seed(
            Product(id = 1, name = "Old", category = "Dairy products",
                expiryEpochDay = LocalDate.now().minusDays(2).toEpochDay(), stocked = true)
        )
        // Construct after seeding: the sweep runs against existing data on open.
        viewModel = createViewModel()

        dispatcher.scheduler.advanceUntilIdle()

        assertTrue(fakeDao.current().first { it.id == 1L }.expired)
    }

    @Test
    fun `markConsumed updates product state`() = runTest {
        val product = Product(id = 1, name = "Milk", category = "Dairy products",
            expiryEpochDay = LocalDate.now().plusDays(5).toEpochDay(), stocked = true)
        fakeDao.seed(product)
        viewModel = createViewModel()

        viewModel.markConsumed(product)
        dispatcher.scheduler.advanceUntilIdle()

        val updated = fakeDao.current().first { it.id == 1L }
        assertTrue(updated.consumed)
        assertTrue(!updated.stocked)
    }
}

/** In-memory fake mirroring the DAO contract. */
class FakeProductDao : ProductDao {
    private val products = MutableStateFlow<List<Product>>(emptyList())
    private var nextId = 1L

    fun seed(vararg items: Product) {
        products.value = items.toList()
        nextId = (items.maxOfOrNull { it.id } ?: 0) + 1
    }

    fun current(): List<Product> = products.value

    override fun stockedProducts(): Flow<List<Product>> =
        products.map { list ->
            list.filter { it.stocked && !it.onShoppingList }.sortedBy { it.expiryEpochDay }
        }

    override fun shoppingListProducts(): Flow<List<Product>> =
        products.map { list -> list.filter { it.onShoppingList }.sortedByDescending { it.id } }

    override fun timelineProducts(): Flow<List<Product>> =
        products.map { list -> list.filterNot { it.onShoppingList }.sortedBy { it.expiryEpochDay } }

    override suspend fun expiringSoon(todayEpochDay: Long): List<Product> =
        products.value.filter {
            it.stocked && !it.onShoppingList &&
                it.expiryEpochDay in todayEpochDay..(todayEpochDay + 1)
        }

    override suspend fun insert(product: Product): Long {
        val id = nextId++
        products.value += product.copy(id = id)
        return id
    }

    override suspend fun update(product: Product) {
        products.value = products.value.map { if (it.id == product.id) product else it }
    }

    override suspend fun delete(product: Product) {
        products.value = products.value.filterNot { it.id == product.id }
    }

    override suspend fun markOverdueAsExpired(todayEpochDay: Long): Int {
        var count = 0
        products.value = products.value.map {
            if (it.stocked && !it.onShoppingList &&
                (it.expiryEpochDay ?: Long.MAX_VALUE) < todayEpochDay
            ) {
                count++
                it.copy(expired = true, stocked = false, consumed = false)
            } else it
        }
        return count
    }
}
