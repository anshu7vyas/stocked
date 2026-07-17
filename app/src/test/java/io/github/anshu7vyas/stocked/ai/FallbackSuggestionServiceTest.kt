package io.github.anshu7vyas.stocked.ai

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class FallbackSuggestionServiceTest {

    @Test
    fun `primary result is preferred when available`() = runTest {
        val primary = FakeSuggestionService(Suggestion("Dairy products", 7))
        val fallback = FakeSuggestionService(Suggestion("Dishes", 3))
        val service = FallbackSuggestionService(primary, fallback)

        assertEquals(Suggestion("Dairy products", 7), service.suggest("Milk"))
        assertEquals(0, fallback.calls)
    }

    @Test
    fun `falls back to lookup when primary has no suggestion`() = runTest {
        val primary = FakeSuggestionService(null) // Nano unavailable / failed / timed out
        val fallback = FakeSuggestionService(Suggestion("Dairy products", 7))
        val service = FallbackSuggestionService(primary, fallback)

        assertEquals(Suggestion("Dairy products", 7), service.suggest("Milk"))
        assertEquals(1, primary.calls)
        assertEquals(1, fallback.calls)
    }

    @Test
    fun `returns null when neither service knows the item`() = runTest {
        val service = FallbackSuggestionService(
            FakeSuggestionService(null),
            FakeSuggestionService(null),
        )

        assertNull(service.suggest("Mystery item"))
    }
}

/** In-memory fake returning a fixed result and counting invocations. */
class FakeSuggestionService(private val result: Suggestion?) : SuggestionService {
    var calls = 0
        private set

    override suspend fun suggest(name: String): Suggestion? {
        calls++
        return result
    }
}
