package io.github.anshu7vyas.stocked.appfunctions

import android.content.Context
import androidx.appfunctions.AppFunctionContext
import androidx.appfunctions.AppFunctionInvalidArgumentException
import io.github.anshu7vyas.stocked.ai.Suggestion
import io.github.anshu7vyas.stocked.ai.SuggestionCategories
import io.github.anshu7vyas.stocked.data.Product
import io.github.anshu7vyas.stocked.data.ProductRepository
import io.github.anshu7vyas.stocked.ui.home.FakeProductDao
import io.github.anshu7vyas.stocked.ui.home.FakeSuggestionService
import java.time.LocalDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

// Robolectric: AppFunctionInvalidArgumentException defaults its extras to Bundle.EMPTY,
// which is null in the plain-JVM android.jar stub. The dispatcher must be a test one:
// real Dispatchers.IO threads deadlock Robolectric's sandboxed main thread.
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class PantryAppFunctionsTest {

    private lateinit var fakeDao: FakeProductDao
    private lateinit var fakeSuggestions: FakeSuggestionService
    private lateinit var functions: PantryAppFunctions

    /** The context is a pass-through the functions never touch. */
    private val appFunctionContext = object : AppFunctionContext {
        override val context: Context
            get() = throw UnsupportedOperationException("not used")
    }

    @Before
    fun setUp() {
        fakeDao = FakeProductDao()
        fakeSuggestions = FakeSuggestionService()
        functions = PantryAppFunctions(ProductRepository(fakeDao), fakeSuggestions, UnconfinedTestDispatcher())
    }

    @Test
    fun `addPantryItem stores explicit category and expiry`() = runTest {
        val expiry = LocalDate.now().plusDays(5)

        val item = functions.addPantryItem(
            appFunctionContext, "Milk", SuggestionCategories.DAIRY_PRODUCTS, expiry.toString(),
        )

        assertEquals(PantryItem("Milk", SuggestionCategories.DAIRY_PRODUCTS, expiry.toString(), 5), item)
        val stored = fakeDao.current().single()
        assertTrue(stored.stocked)
        assertEquals(expiry.toEpochDay(), stored.expiryEpochDay)
        // Explicit arguments never consult the suggestion service.
        assertEquals(0, fakeSuggestions.suggestCalls)
    }

    @Test
    fun `addPantryItem infers category and expiry from the name`() = runTest {
        fakeSuggestions.put("Milk", Suggestion(SuggestionCategories.DAIRY_PRODUCTS, 7))

        val item = functions.addPantryItem(appFunctionContext, "  Milk  ")

        assertEquals(SuggestionCategories.DAIRY_PRODUCTS, item.category)
        assertEquals(LocalDate.now().plusDays(7).toString(), item.expiryDate)
        assertEquals(7, item.daysLeft)
        assertEquals("Milk", fakeDao.current().single().name)
    }

    @Test
    fun `addPantryItem rejects unknown name with nothing to infer`() = runTest {
        assertInvalidArgument { functions.addPantryItem(appFunctionContext, "Mystery item") }
        assertTrue(fakeDao.current().isEmpty())
    }

    @Test
    fun `addPantryItem rejects blank name, bad category, bad date, and non-future expiry`() = runTest {
        assertInvalidArgument { functions.addPantryItem(appFunctionContext, "   ") }
        assertInvalidArgument {
            functions.addPantryItem(appFunctionContext, "Milk", "Beverages", LocalDate.now().plusDays(3).toString())
        }
        assertInvalidArgument {
            functions.addPantryItem(
                appFunctionContext, "Milk", SuggestionCategories.DAIRY_PRODUCTS, "next tuesday",
            )
        }
        // Legacy rule: minimum expiry is tomorrow — today itself is not addable.
        assertInvalidArgument {
            functions.addPantryItem(
                appFunctionContext, "Milk", SuggestionCategories.DAIRY_PRODUCTS, LocalDate.now().toString(),
            )
        }
        assertTrue(fakeDao.current().isEmpty())
    }

    @Test
    fun `addPantryItem uses inferred shelf life when explicit category matches the inference`() = runTest {
        fakeSuggestions.put("Milk", Suggestion(SuggestionCategories.DAIRY_PRODUCTS, 7))

        val item = functions.addPantryItem(appFunctionContext, "Milk", SuggestionCategories.DAIRY_PRODUCTS)

        assertEquals(LocalDate.now().plusDays(7).toString(), item.expiryDate)
    }

    @Test
    fun `addPantryItem refuses to borrow a shelf life inferred for a different category`() = runTest {
        // Freezing milk: the caller overrides the category, so the 7-day fresh-dairy
        // shelf life must not be applied — an explicit expiryDate is required instead.
        fakeSuggestions.put("Milk", Suggestion(SuggestionCategories.DAIRY_PRODUCTS, 7))

        assertInvalidArgument {
            functions.addPantryItem(appFunctionContext, "Milk", SuggestionCategories.FROZEN_ITEMS)
        }
        assertTrue(fakeDao.current().isEmpty())
    }

    @Test
    fun `addPantryItem rejects an expiry date beyond the 100-year horizon`() = runTest {
        assertInvalidArgument {
            functions.addPantryItem(
                appFunctionContext, "Milk", SuggestionCategories.DAIRY_PRODUCTS, "+999999999-12-31",
            )
        }
        assertTrue(fakeDao.current().isEmpty())
    }

    @Test
    fun `addToShoppingList infers category and stores uncategorized on a miss`() = runTest {
        fakeSuggestions.put("Paneer", Suggestion(SuggestionCategories.DAIRY_PRODUCTS, 5))

        val known = functions.addToShoppingList(appFunctionContext, "Paneer")
        val unknown = functions.addToShoppingList(appFunctionContext, "Mystery item")

        assertEquals(ShoppingListItem("Paneer", SuggestionCategories.DAIRY_PRODUCTS), known)
        assertEquals(ShoppingListItem("Mystery item", ""), unknown)
        val stored = fakeDao.current()
        assertTrue(stored.all { it.onShoppingList && !it.stocked })
        assertEquals(SuggestionCategories.DAIRY_PRODUCTS, stored.first { it.name == "Paneer" }.category)
    }

    @Test
    fun `queryExpiringItems returns the notification window soonest-first`() = runTest {
        val today = LocalDate.now()
        fakeDao.seed(
            Product(id = 1, name = "Yogurt", category = SuggestionCategories.DAIRY_PRODUCTS,
                expiryEpochDay = today.plusDays(1).toEpochDay(), stocked = true),
            Product(id = 2, name = "Spinach", category = SuggestionCategories.GREEN_VEGETABLES,
                expiryEpochDay = today.toEpochDay(), stocked = true),
            Product(id = 3, name = "Peas", category = SuggestionCategories.FROZEN_ITEMS,
                expiryEpochDay = today.plusDays(30).toEpochDay(), stocked = true),
        )

        val expiring = functions.queryExpiringItems(appFunctionContext)

        assertEquals(listOf("Spinach", "Yogurt"), expiring.map { it.name })
        // Legacy semantics: the expiry date itself counts as 1 day left, and expiring
        // tomorrow is also 1 day (diff == 0 -> 1, else diff) — both windows read "1".
        assertEquals(1, expiring.first { it.name == "Spinach" }.daysLeft)
        assertEquals(1, expiring.first { it.name == "Yogurt" }.daysLeft)
    }

    private suspend fun assertInvalidArgument(block: suspend () -> Unit) {
        val thrown = runCatching { block() }.exceptionOrNull()
        assertTrue(
            "expected AppFunctionInvalidArgumentException, got $thrown",
            thrown is AppFunctionInvalidArgumentException,
        )
    }
}
