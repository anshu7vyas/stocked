package io.github.anshu7vyas.stocked.ai

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Realistic 30-item grocery corpus for the offline lookup service. Prints the
 * per-item table and accuracy to stdout (recorded in MIGRATION_LOG) and gates
 * the suite at 80% accuracy. Items the table honestly can't place (bread, fresh
 * fish, dips) are expected misses — returning null beats guessing wildly.
 */
class SuggestionQualityCorpusTest {

    private val corpus = listOf(
        "Whole milk" to "Dairy products",
        "Greek yogurt" to "Dairy products",
        "Cheddar cheese" to "Dairy products",
        "Salted butter" to "Dairy products",
        "Eggs" to "Dairy products",
        "Paneer" to "Dairy products",
        "Sour cream" to "Dairy products",
        "Baby spinach" to "Green vegetables",
        "Romaine lettuce" to "Green vegetables",
        "Kale" to "Green vegetables",
        "Broccoli" to "Green vegetables",
        "Cucumber" to "Green vegetables",
        "Green beans" to "Green vegetables",
        "Bell peppers" to "Green vegetables",
        "Fresh coriander" to "Green vegetables",
        "Zucchini" to "Green vegetables",
        "Cherry tomatoes" to "Green vegetables",
        "Frozen peas" to "Frozen Items",
        "Vanilla ice cream" to "Frozen Items",
        "Frozen pizza" to "Frozen Items",
        "Fish fingers" to "Frozen Items",
        "Chicken nuggets" to "Frozen Items",
        "Frozen berries" to "Frozen Items",
        "Leftover pasta" to "Dishes",
        "Chicken curry" to "Dishes",
        "Vegetable soup" to "Dishes",
        "Dal tadka" to "Dishes",
        "Chicken biryani" to "Dishes",
        "Sourdough bread" to "Dishes", // expected miss: bread has no honest bucket
        "Hummus" to "Dishes", // expected miss: dips aren't in the table
    )

    @Test
    fun `lookup service is at least 80 percent accurate on the corpus`() = runTest {
        val service = LocalLookupSuggestionService()
        var hits = 0

        println("%-20s | %-16s | %-22s | %s".format("Item", "Expected", "Actual", "Result"))
        println("-".repeat(72))
        corpus.forEach { (item, expected) ->
            val actual = service.suggest(item)
            val hit = actual?.category == expected
            if (hit) hits++
            actual?.let {
                assertTrue(
                    "'$item' produced invalid category '${it.category}'",
                    it.category in SuggestionCategories.ALL,
                )
            }
            val actualLabel = actual?.let { "${it.category} (${it.shelfLifeDays}d)" } ?: "(none)"
            println("%-20s | %-16s | %-22s | %s".format(item, expected, actualLabel, if (hit) "HIT" else "MISS"))
        }

        val accuracy = hits * 100.0 / corpus.size
        println("-".repeat(72))
        println("Accuracy: $hits/${corpus.size} = ${"%.1f".format(accuracy)}%")
        assertTrue("Corpus accuracy $accuracy% is below the 80% gate", accuracy >= 80.0)
    }
}
