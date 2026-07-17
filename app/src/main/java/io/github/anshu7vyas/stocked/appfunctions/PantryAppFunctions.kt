package io.github.anshu7vyas.stocked.appfunctions

import androidx.appfunctions.AppFunctionContext
import androidx.appfunctions.AppFunctionInvalidArgumentException
import androidx.appfunctions.AppFunctionSerializable
import androidx.appfunctions.service.AppFunction
import io.github.anshu7vyas.stocked.ai.SuggestionCategories
import io.github.anshu7vyas.stocked.ai.SuggestionService
import io.github.anshu7vyas.stocked.data.ProductRepository
import io.github.anshu7vyas.stocked.di.IoDispatcher
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * A pantry item with its expiry state.
 */
@AppFunctionSerializable(isDescribedByKDoc = true)
data class PantryItem(
    /** Item name as stored in the pantry. */
    val name: String,
    /** One of: "Frozen Items", "Green vegetables", "Dairy products", "Dishes". */
    val category: String,
    /** Expiry date in ISO-8601 format (YYYY-MM-DD). The item counts as expired from the day AFTER this date. */
    val expiryDate: String,
    /** Days of freshness remaining. The expiry date itself counts as 1 day left. */
    val daysLeft: Int,
)

/**
 * An item on the shopping list.
 */
@AppFunctionSerializable(isDescribedByKDoc = true)
data class ShoppingListItem(
    /** Item name as stored on the shopping list. */
    val name: String,
    /** One of: "Frozen Items", "Green vegetables", "Dairy products", "Dishes"; empty when uncategorized. */
    val category: String,
)

/**
 * Stocked's [AppFunction]s: agent-facing pantry and shopping-list operations
 * over [ProductRepository], with [SuggestionService] filling in category and
 * shelf life when the caller omits them.
 */
@Singleton
class PantryAppFunctions @Inject constructor(
    private val repository: ProductRepository,
    private val suggestionService: SuggestionService,
    // AppFunction implementations are invoked on the main thread; hop off it.
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) {
    /**
     * Add an item to the pantry with an expiry date.
     * When [category] or [expiryDate] is omitted, on-device AI infers them from the
     * item name; unrecognized names then fail and need explicit values.
     *
     * @param appFunctionContext The execution context.
     * @param name The grocery item name. Must not be blank.
     * @param category Pantry category, one of: "Frozen Items", "Green vegetables",
     *   "Dairy products", "Dishes". If null, inferred from [name].
     * @param expiryDate Expiry date in ISO-8601 format (YYYY-MM-DD); must be tomorrow
     *   or later (and within 100 years). If null, inferred from the item's typical shelf
     *   life — but only when [category] was also inferred or matches the inference;
     *   an overridden category needs an explicit expiry.
     * @return The stored [PantryItem] with the resolved category and expiry.
     * @throws AppFunctionInvalidArgumentException If [name] is blank, [category] is not one
     *   of the four listed values, [expiryDate] is malformed, today or earlier, or
     *   unreasonably far out, or inference failed — retry with an explicit
     *   [category]/[expiryDate].
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun addPantryItem(
        appFunctionContext: AppFunctionContext,
        name: String,
        category: String? = null,
        expiryDate: String? = null,
    ): PantryItem = withContext(ioDispatcher) {
        val trimmed = requireName(name)
        category?.let(::requireValidCategory)
        val today = LocalDate.now()
        val parsedExpiry = expiryDate?.let {
            runCatching { LocalDate.parse(it) }.getOrElse {
                throw AppFunctionInvalidArgumentException(
                    "expiryDate must be an ISO-8601 date (YYYY-MM-DD), got \"$expiryDate\""
                )
            }
        }
        if (parsedExpiry != null && !parsedExpiry.isAfter(today)) {
            throw AppFunctionInvalidArgumentException(
                "expiryDate must be tomorrow or later (got $parsedExpiry; today is $today)"
            )
        }
        // Upper bound keeps daysLeft (an Int, here and in Product.daysLeft) from truncating.
        if (parsedExpiry != null && parsedExpiry.isAfter(today.plusYears(MAX_EXPIRY_YEARS))) {
            throw AppFunctionInvalidArgumentException(
                "expiryDate must be within $MAX_EXPIRY_YEARS years (got $parsedExpiry)"
            )
        }

        val suggestion =
            if (category == null || parsedExpiry == null) suggestionService.suggest(trimmed) else null
        val resolvedCategory = category
            ?: suggestion?.category
            ?: throw AppFunctionInvalidArgumentException(
                "Could not infer a category for \"$trimmed\" — pass category explicitly " +
                    "(one of: ${SuggestionCategories.ALL.joinToString()})"
            )
        // A suggested shelf life is only valid for the category it was suggested with:
        // "Milk" stored as Frozen Items must not inherit the 7-day fresh-dairy expiry.
        val resolvedExpiry = parsedExpiry
            ?: suggestion?.takeIf { it.category == resolvedCategory }
                ?.let { today.plusDays(it.shelfLifeDays.toLong()) }
            ?: throw AppFunctionInvalidArgumentException(
                "Could not infer a shelf life for \"$trimmed\" as $resolvedCategory — " +
                    "pass expiryDate explicitly (tomorrow or later)"
            )

        repository.addStockedProduct(trimmed, resolvedCategory, resolvedExpiry)
        // resolvedExpiry is strictly after today (validated above, and suggested shelf
        // lives are >= 1 day), so the legacy 0 -> "1 day left" boundary can't occur here.
        val daysLeft = (resolvedExpiry.toEpochDay() - today.toEpochDay()).toInt()
        PantryItem(trimmed, resolvedCategory, resolvedExpiry.toString(), daysLeft)
    }

    /**
     * Add an item to the shopping list.
     * When [category] is omitted, on-device AI infers it from the item name;
     * unrecognized names are stored uncategorized. A categorized shopping item
     * pre-selects its category when later moved to the pantry.
     *
     * @param appFunctionContext The execution context.
     * @param name The grocery item name. Must not be blank.
     * @param category Pantry category, one of: "Frozen Items", "Green vegetables",
     *   "Dairy products", "Dishes". If null, inferred from [name] when possible.
     * @return The stored [ShoppingListItem]; its category is empty if none was inferred.
     * @throws AppFunctionInvalidArgumentException If [name] is blank or [category] is not
     *   one of the four listed values.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun addToShoppingList(
        appFunctionContext: AppFunctionContext,
        name: String,
        category: String? = null,
    ): ShoppingListItem = withContext(ioDispatcher) {
        val trimmed = requireName(name)
        category?.let(::requireValidCategory)

        val resolvedCategory = category ?: suggestionService.suggest(trimmed)?.category.orEmpty()
        repository.addShoppingItem(trimmed, resolvedCategory)
        ShoppingListItem(trimmed, resolvedCategory)
    }

    /**
     * List pantry items expiring today or tomorrow — the same set the app's
     * expiry notification reports.
     *
     * @param appFunctionContext The execution context.
     * @return Expiring [PantryItem]s soonest-first; empty if nothing expires in the window.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun queryExpiringItems(
        appFunctionContext: AppFunctionContext,
    ): List<PantryItem> = withContext(ioDispatcher) {
        val today = LocalDate.now()
        repository.expiringSoon(today)
            .mapNotNull { product ->
                val expiryEpochDay = product.expiryEpochDay ?: return@mapNotNull null
                val daysLeft = product.daysLeft(today) ?: return@mapNotNull null
                PantryItem(product.name, product.category, LocalDate.ofEpochDay(expiryEpochDay).toString(), daysLeft)
            }
            .sortedBy { it.expiryDate }
    }

    private fun requireName(name: String): String =
        name.trim().ifEmpty { throw AppFunctionInvalidArgumentException("name must not be blank") }

    private fun requireValidCategory(category: String) {
        if (category !in SuggestionCategories.ALL) {
            throw AppFunctionInvalidArgumentException(
                "Unknown category \"$category\" — must be one of: ${SuggestionCategories.ALL.joinToString()}"
            )
        }
    }

    private companion object {
        const val MAX_EXPIRY_YEARS = 100L
    }
}
