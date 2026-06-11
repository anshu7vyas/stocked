package io.github.anshu7vyas.stocked.ui.shopping

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.anshu7vyas.stocked.data.Product
import io.github.anshu7vyas.stocked.data.ProductRepository
import io.github.anshu7vyas.stocked.di.ApplicationScope
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class ShoppingViewModel @Inject constructor(
    private val repository: ProductRepository,
    @param:ApplicationScope private val applicationScope: CoroutineScope,
) : ViewModel() {

    val items: StateFlow<List<Product>> =
        repository.shoppingListProducts()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** Application scope: ShoppingItemActivity finishes right after this call. */
    fun addItem(name: String) {
        applicationScope.launch { repository.addShoppingItem(name) }
    }

    fun delete(product: Product) {
        viewModelScope.launch { repository.delete(product) }
    }
}
