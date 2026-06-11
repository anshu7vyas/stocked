package io.github.anshu7vyas.stocked.ui.additem

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.anshu7vyas.stocked.data.ProductRepository
import io.github.anshu7vyas.stocked.di.ApplicationScope
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@HiltViewModel
class AddItemViewModel @Inject constructor(
    private val repository: ProductRepository,
    @param:ApplicationScope private val applicationScope: CoroutineScope,
) : ViewModel() {

    /**
     * Launched on the application scope, not viewModelScope: the caller finishes the
     * activity immediately, and a viewModelScope write would be cancelled with it.
     */
    fun addStockedProduct(name: String, category: String, expiry: LocalDate) {
        applicationScope.launch { repository.addStockedProduct(name, category, expiry) }
    }
}
