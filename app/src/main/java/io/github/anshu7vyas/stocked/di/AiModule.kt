package io.github.anshu7vyas.stocked.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.anshu7vyas.stocked.ai.FallbackSuggestionService
import io.github.anshu7vyas.stocked.ai.GeminiNanoSuggestionService
import io.github.anshu7vyas.stocked.ai.LocalLookupSuggestionService
import io.github.anshu7vyas.stocked.ai.SuggestionService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AiModule {

    /** Gemini Nano when the device supports it; the bundled lookup table otherwise. */
    @Provides
    @Singleton
    fun provideSuggestionService(): SuggestionService =
        FallbackSuggestionService(
            primary = GeminiNanoSuggestionService(),
            fallback = LocalLookupSuggestionService(),
        )
}
