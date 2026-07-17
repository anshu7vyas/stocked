package io.github.anshu7vyas.stocked.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.anshu7vyas.stocked.ai.NameSuggestion
import io.github.anshu7vyas.stocked.ai.SuggestionService
import io.github.anshu7vyas.stocked.data.Product
import io.github.anshu7vyas.stocked.data.ProductRepository
import io.github.anshu7vyas.stocked.di.ApplicationScope
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val items: List<StockedItem> = emptyList(),
)

data class StockedItem(
    val product: Product,
    val daysLeft: Int,
)

/** Wait for a pause in typing before querying the suggestion service. */
private const val SUGGESTION_DEBOUNCE_MS = 300L

/** Names shorter than this are too ambiguous to be worth a suggestion query. */
private const val SUGGESTION_MIN_LENGTH = 3

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ProductRepository,
    private val suggestionService: SuggestionService,
    @param:ApplicationScope private val applicationScope: CoroutineScope,
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> =
        repository.stockedProducts()
            .map { products ->
                val today = LocalDate.now()
                HomeUiState(products.map { StockedItem(it, it.daysLeft(today) ?: Int.MAX_VALUE) })
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    private val nameInput = MutableStateFlow("")

    /**
     * Category + shelf-life suggestion for the add-sheet name input, tagged with
     * the trimmed name it was computed for so the UI can drop a proposal once
     * the field diverges from it. Debounced so the service is queried once per
     * typing pause; blank or too-short input maps to null, and mapLatest cancels
     * a stale query when the name changes.
     */
    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val suggestion: StateFlow<NameSuggestion?> =
        nameInput
            .debounce(SUGGESTION_DEBOUNCE_MS)
            .mapLatest { input ->
                input.trim()
                    .takeIf { it.length >= SUGGESTION_MIN_LENGTH }
                    ?.let { trimmed ->
                        suggestionService.suggest(trimmed)?.let { NameSuggestion(trimmed, it) }
                    }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    init {
        // The legacy app swept overdue items every time Home opened; keep that
        // behavior in addition to the daily worker.
        viewModelScope.launch { repository.expireOverdue() }
    }

    /** Called on every keystroke in the add-sheet name field. */
    fun onNameChanged(name: String) {
        nameInput.value = name
    }

    /** Resets the suggestion pipeline when the add sheet closes. */
    fun clearSuggestion() {
        nameInput.value = ""
    }

    fun markConsumed(product: Product) {
        viewModelScope.launch { repository.markConsumed(product) }
    }

    fun markExpired(product: Product) {
        viewModelScope.launch { repository.markExpired(product) }
    }

    fun delete(product: Product) {
        viewModelScope.launch { repository.delete(product) }
    }

    /**
     * Saves the add-sheet result: a brand-new pantry item, or a shopping-list
     * item being moved (with possibly edited name/category). Application scope:
     * the write must persist even if Home leaves composition mid-write.
     */
    fun saveToPantry(moving: Product?, name: String, category: String, expiry: LocalDate) {
        applicationScope.launch {
            if (moving != null) {
                repository.moveToPantry(moving.copy(name = name, category = category), expiry)
            } else {
                repository.addStockedProduct(name, category, expiry)
            }
        }
    }
}
