package io.github.anshu7vyas.stocked.ai

/**
 * A category + shelf-life proposal for a grocery item name.
 *
 * [category] is always one of [SuggestionCategories.ALL] — the legacy category
 * strings stored verbatim in the database.
 */
data class Suggestion(
    val category: String,
    val shelfLifeDays: Int,
)

/**
 * A [Suggestion] tagged with the trimmed [name] it was computed for. A debounced
 * query can resolve after the user has kept typing, so the UI must be able to
 * tell whether a proposal still matches the current input before offering Apply.
 */
data class NameSuggestion(
    val name: String,
    val suggestion: Suggestion,
)

/** The four legacy DB category strings; suggestions must never invent others. */
object SuggestionCategories {
    const val FROZEN_ITEMS = "Frozen Items"
    const val GREEN_VEGETABLES = "Green vegetables"
    const val DAIRY_PRODUCTS = "Dairy products"
    const val DISHES = "Dishes"

    val ALL = setOf(FROZEN_ITEMS, GREEN_VEGETABLES, DAIRY_PRODUCTS, DISHES)
}
