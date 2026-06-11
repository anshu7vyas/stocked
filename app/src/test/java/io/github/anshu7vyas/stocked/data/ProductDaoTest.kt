package io.github.anshu7vyas.stocked.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import java.time.LocalDate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ProductDaoTest {

    private lateinit var database: StockedDatabase
    private lateinit var dao: ProductDao

    private val today: LocalDate = LocalDate.of(2026, 6, 10)

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, StockedDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.productDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    private fun stocked(name: String, expiry: LocalDate) = Product(
        name = name, category = "Dairy products",
        expiryEpochDay = expiry.toEpochDay(), stocked = true,
    )

    @Test
    fun `stocked products exclude shopping items and sort by expiry`() = runTest {
        dao.insert(stocked("Later", today.plusDays(30)))
        dao.insert(stocked("Soon", today.plusDays(2)))
        dao.insert(Product(name = "Eggs", category = "", onShoppingList = true))

        val stocked = dao.stockedProducts().first()
        assertEquals(listOf("Soon", "Later"), stocked.map { it.name })
    }

    @Test
    fun `cross-year expiry sorts correctly - the legacy lexicographic bug`() = runTest {
        // As MM/dd/yyyy strings, "01/02/2027" < "12/20/2026" lexicographically,
        // so the legacy app sorted the January item first. Epoch days sort right.
        dao.insert(stocked("January", LocalDate.of(2027, 1, 2)))
        dao.insert(stocked("December", LocalDate.of(2026, 12, 20)))

        val sorted = dao.stockedProducts().first()
        assertEquals(listOf("December", "January"), sorted.map { it.name })
    }

    @Test
    fun `markOverdueAsExpired flips only overdue stocked items`() = runTest {
        dao.insert(stocked("Overdue", today.minusDays(1)))
        dao.insert(stocked("ExpiresToday", today))
        dao.insert(stocked("Fresh", today.plusDays(5)))

        val flipped = dao.markOverdueAsExpired(today.toEpochDay())

        assertEquals(1, flipped)
        val all = dao.timelineProducts().first()
        val overdue = all.first { it.name == "Overdue" }
        assertTrue(overdue.expired)
        assertTrue(!overdue.stocked)
        // Expiry day itself is still "1 day left" — not expired.
        assertTrue(all.first { it.name == "ExpiresToday" }.stocked)
    }

    @Test
    fun `expiringSoon returns items expiring today or tomorrow`() = runTest {
        dao.insert(stocked("Today", today))
        dao.insert(stocked("Tomorrow", today.plusDays(1)))
        dao.insert(stocked("NextWeek", today.plusDays(7)))

        val expiring = dao.expiringSoon(today.toEpochDay())
        assertEquals(setOf("Today", "Tomorrow"), expiring.map { it.name }.toSet())
    }

    @Test
    fun `shopping list returns only shopping items`() = runTest {
        dao.insert(Product(name = "Eggs", category = "", onShoppingList = true))
        dao.insert(stocked("Milk", today.plusDays(3)))

        val shopping = dao.shoppingListProducts().first()
        assertEquals(listOf("Eggs"), shopping.map { it.name })
    }
}
