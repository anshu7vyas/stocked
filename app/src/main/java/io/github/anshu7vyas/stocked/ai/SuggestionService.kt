package io.github.anshu7vyas.stocked.ai

/** Proposes a category and shelf life for a grocery item name. */
interface SuggestionService {

    /** Returns a suggestion for [name], or null when nothing confident is known. */
    suspend fun suggest(name: String): Suggestion?
}
