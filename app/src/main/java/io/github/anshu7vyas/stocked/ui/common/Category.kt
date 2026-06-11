package io.github.anshu7vyas.stocked.ui.common

/** Emoji decoration for the legacy category names (kept verbatim in the DB). */
fun categoryEmoji(category: String): String = when (category) {
    "Frozen Items" -> "❄️"
    "Green vegetables" -> "🥬"
    "Dairy products" -> "🥛"
    "Dishes" -> "🍽️"
    else -> "🛒"
}
