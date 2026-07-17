package io.github.anshu7vyas.stocked.ai

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LocalLookupSuggestionServiceTest {

    private val service = LocalLookupSuggestionService()

    @Test
    fun `known item returns its category and shelf life`() = runTest {
        assertEquals(Suggestion("Dairy products", 7), service.suggest("Milk"))
    }

    @Test
    fun `keyword matches inside a longer name`() = runTest {
        assertEquals(Suggestion("Dairy products", 7), service.suggest("Organic skim milk"))
    }

    @Test
    fun `matching is case-insensitive`() = runTest {
        assertEquals(Suggestion("Green vegetables", 5), service.suggest("SPINACH"))
    }

    @Test
    fun `plural names match singular keywords`() = runTest {
        assertEquals(Suggestion("Dairy products", 21), service.suggest("Eggs"))
        assertEquals(Suggestion("Green vegetables", 6), service.suggest("Tomatoes"))
    }

    @Test
    fun `longest keyword wins on overlap`() = runTest {
        // "frozen" beats the pea entry; "paneer tikka" beats the paneer entry.
        assertEquals(Suggestion("Frozen Items", 90), service.suggest("Frozen peas"))
        assertEquals(Suggestion("Dishes", 3), service.suggest("Paneer tikka"))
    }

    @Test
    fun `keyword does not match as a substring of another word`() = runTest {
        // "pea" must not match inside "peanut", "milk" not inside "buttermilk"
        // (which has its own entry).
        assertNull(service.suggest("Peanut brittle"))
        assertEquals(Suggestion("Dairy products", 10), service.suggest("Buttermilk"))
    }

    @Test
    fun `unknown and blank names return null`() = runTest {
        assertNull(service.suggest("Quinoa"))
        assertNull(service.suggest("   "))
    }

    @Test
    fun `every table entry emits a legacy category`() {
        LocalLookupSuggestionService.entries.forEach { entry ->
            assertTrue(
                "'${entry.keyword}' maps to unknown category '${entry.category}'",
                entry.category in SuggestionCategories.ALL,
            )
        }
    }

    @Test
    fun `every table entry has a positive shelf life`() {
        LocalLookupSuggestionService.entries.forEach { entry ->
            assertTrue(
                "'${entry.keyword}' has shelf life ${entry.shelfLifeDays}",
                entry.shelfLifeDays in 1..365,
            )
        }
    }
}
