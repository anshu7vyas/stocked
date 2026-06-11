package io.github.anshu7vyas.stocked.ui.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.anshu7vyas.stocked.data.Product
import io.github.anshu7vyas.stocked.data.ProductRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class TimelineViewModel @Inject constructor(
    repository: ProductRepository,
) : ViewModel() {

    val items: StateFlow<List<Product>> =
        repository.timelineProducts()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
