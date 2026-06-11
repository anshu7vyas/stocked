package io.github.anshu7vyas.stocked.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.anshu7vyas.stocked.data.Product
import io.github.anshu7vyas.stocked.data.ProductRepository
import io.github.anshu7vyas.stocked.di.ApplicationScope
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val items: List<StockedItem> = emptyList(),
)

data class StockedItem(
    val product: Product,
    val daysLeft: Int,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ProductRepository,
    @param:ApplicationScope private val applicationScope: CoroutineScope,
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> =
        repository.stockedProducts()
            .map { products ->
                val today = LocalDate.now()
                HomeUiState(products.map { StockedItem(it, it.daysLeft(today) ?: Int.MAX_VALUE) })
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    init {
        // The legacy app swept overdue items every time Home opened; keep that
        // behavior in addition to the daily worker.
        viewModelScope.launch { repository.expireOverdue() }
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
