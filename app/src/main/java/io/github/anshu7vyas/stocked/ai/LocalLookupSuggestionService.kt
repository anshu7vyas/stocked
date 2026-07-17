package io.github.anshu7vyas.stocked.ai

/**
 * Deterministic offline suggestions from a bundled keyword table.
 *
 * Matching is case-insensitive, whole-word (with an optional plural suffix), and
 * the longest matching keyword wins — so "frozen peas" resolves to Frozen Items
 * rather than the peas entry, and "paneer tikka" to Dishes rather than Dairy
 * products. Fresh produce maps to "Green vegetables", the app's only produce
 * category. Unknown names return null; the table never guesses.
 */
class LocalLookupSuggestionService : SuggestionService {

    override suspend fun suggest(name: String): Suggestion? {
        if (name.isBlank()) return null
        return entries
            .filter { it.regex.containsMatchIn(name) }
            .maxByOrNull { it.keyword.length }
            ?.let { Suggestion(it.category, it.shelfLifeDays) }
    }

    internal class LookupEntry(
        val keyword: String,
        val category: String,
        val shelfLifeDays: Int,
    ) {
        val regex = Regex("""\b${Regex.escape(keyword)}(s|es)?\b""", RegexOption.IGNORE_CASE)
    }

    internal companion object {
        internal val entries: List<LookupEntry> = buildList {
            fun add(category: String, vararg items: Pair<String, Int>) {
                items.forEach { (keyword, days) -> add(LookupEntry(keyword, category, days)) }
            }

            add(
                SuggestionCategories.DAIRY_PRODUCTS,
                "milk" to 7,
                "buttermilk" to 10,
                "condensed milk" to 30,
                "yogurt" to 14,
                "yoghurt" to 14,
                "curd" to 7,
                "kefir" to 14,
                "lassi" to 3,
                "custard" to 3,
                "cheese" to 21,
                "cheddar" to 21,
                "mozzarella" to 14,
                "feta" to 30,
                "parmesan" to 60,
                "brie" to 14,
                "ricotta" to 7,
                "mascarpone" to 7,
                "cream cheese" to 14,
                "cottage cheese" to 10,
                "cream" to 7,
                "sour cream" to 14,
                "butter" to 60,
                "ghee" to 180,
                "paneer" to 7,
                "egg" to 21,
            )

            add(
                SuggestionCategories.GREEN_VEGETABLES,
                "spinach" to 5,
                "lettuce" to 7,
                "kale" to 5,
                "arugula" to 5,
                "rocket" to 5,
                "cabbage" to 30,
                "broccoli" to 7,
                "cauliflower" to 7,
                "cucumber" to 7,
                "zucchini" to 7,
                "courgette" to 7,
                "celery" to 14,
                "asparagus" to 4,
                "green bean" to 7,
                "pea" to 5,
                "brussels sprout" to 7,
                "bok choy" to 4,
                "spring onion" to 7,
                "scallion" to 7,
                "leek" to 10,
                "okra" to 4,
                "bell pepper" to 7,
                "capsicum" to 7,
                "green chilli" to 10,
                "coriander" to 5,
                "cilantro" to 5,
                "parsley" to 7,
                "mint" to 5,
                "basil" to 5,
                "dill" to 5,
                "chard" to 5,
                "microgreen" to 3,
                "fenugreek" to 4,
                "methi" to 4,
                "avocado" to 5,
                "tomato" to 6,
                "carrot" to 21,
                "radish" to 10,
                "turnip" to 14,
                "eggplant" to 7,
                "aubergine" to 7,
                "mushroom" to 7,
            )

            add(
                SuggestionCategories.FROZEN_ITEMS,
                "frozen" to 90,
                "ice cream" to 90,
                "gelato" to 90,
                "sorbet" to 90,
                "popsicle" to 90,
                "ice lolly" to 90,
                "fish finger" to 90,
                "fish stick" to 90,
                "nugget" to 90,
                "french fries" to 90,
                "hash brown" to 90,
                "tater tot" to 90,
            )

            add(
                SuggestionCategories.DISHES,
                "curry" to 3,
                "dal" to 3,
                "daal" to 3,
                "biryani" to 3,
                "pulao" to 3,
                "fried rice" to 3,
                "cooked rice" to 4,
                "pasta" to 4,
                "lasagna" to 4,
                "lasagne" to 4,
                "noodle" to 3,
                "soup" to 4,
                "stew" to 4,
                "casserole" to 4,
                "pizza" to 4,
                "sandwich" to 2,
                "burger" to 2,
                "taco" to 2,
                "burrito" to 3,
                "quiche" to 4,
                "meatball" to 4,
                "gravy" to 3,
                "sabzi" to 3,
                "khichdi" to 3,
                "paratha" to 3,
                "roti" to 2,
                "naan" to 2,
                "sushi" to 1,
                "kebab" to 3,
                "samosa" to 3,
                "pakora" to 2,
                "idli" to 2,
                "dosa" to 2,
                "sambar" to 3,
                "rasam" to 3,
                "chole" to 3,
                "rajma" to 3,
                "paneer tikka" to 3,
                "butter chicken" to 3,
                "leftover" to 3,
                "stir fry" to 3,
                "omelette" to 2,
                "pancake" to 2,
                "dumpling" to 3,
                "spring roll" to 3,
                "falafel" to 4,
            )
        }
    }
}
