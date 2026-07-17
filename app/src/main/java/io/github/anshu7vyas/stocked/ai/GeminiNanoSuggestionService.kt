package io.github.anshu7vyas.stocked.ai

import com.google.mlkit.genai.common.FeatureStatus
import com.google.mlkit.genai.prompt.Generation
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.withTimeoutOrNull

/**
 * On-device Gemini Nano suggestions via the ML Kit GenAI Prompt API.
 *
 * Best-effort by design: returns null whenever the device isn't Gemini Nano
 * capable (emulators, non-allowlisted hardware, unlocked bootloaders), the model
 * isn't downloaded yet (download orchestration is deliberately not started from
 * this hot path), the call exceeds the 3s budget, or the model's reply doesn't
 * parse strictly to one of the four legacy categories. Callers fall back to
 * [LocalLookupSuggestionService] — see [FallbackSuggestionService].
 */
class GeminiNanoSuggestionService : SuggestionService {

    override suspend fun suggest(name: String): Suggestion? {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return null
        return try {
            withTimeoutOrNull(TIMEOUT_MS) {
                val model = Generation.getClient()
                try {
                    if (model.checkStatus() != FeatureStatus.AVAILABLE) return@withTimeoutOrNull null
                    parse(model.generateContent(prompt(trimmed)).candidates.firstOrNull()?.text)
                } finally {
                    model.close()
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            // Any SDK failure means "no suggestion"; the lookup fallback covers it.
            null
        }
    }

    private fun prompt(name: String): String =
        """
        You classify grocery items for a pantry app.
        Item: "$name"
        Reply with exactly two lines and nothing else:
        category: <one of Frozen Items | Green vegetables | Dairy products | Dishes>
        shelf_life_days: <how many days the item keeps, an integer between 1 and 365>
        """.trimIndent()

    /** Strict parse: reject anything that isn't exactly a known category plus a sane day count. */
    private fun parse(reply: String?): Suggestion? {
        if (reply.isNullOrBlank()) return null
        var category: String? = null
        var days: Int? = null
        reply.lines().forEach { raw ->
            val line = raw.trim()
            when {
                line.startsWith("category:", ignoreCase = true) ->
                    category = line.substringAfter(":").trim()
                line.startsWith("shelf_life_days:", ignoreCase = true) ->
                    days = line.substringAfter(":").trim().toIntOrNull()
            }
        }
        val canonical = SuggestionCategories.ALL
            .firstOrNull { it.equals(category, ignoreCase = true) } ?: return null
        val validDays = days?.takeIf { it in 1..365 } ?: return null
        return Suggestion(canonical, validDays)
    }

    private companion object {
        const val TIMEOUT_MS = 3_000L
    }
}
