package io.github.anshu7vyas.stocked.ai

/**
 * Tries the on-device model first and falls back to the bundled lookup table,
 * so suggestions degrade gracefully on devices without Gemini Nano.
 */
class FallbackSuggestionService(
    private val primary: SuggestionService,
    private val fallback: SuggestionService,
) : SuggestionService {

    override suspend fun suggest(name: String): Suggestion? =
        primary.suggest(name) ?: fallback.suggest(name)
}
