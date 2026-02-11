package com.tamboo.productslist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tamboo.domain.model.Product
import com.tamboo.domain.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductsListViewModel(
    private val repository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ListUiState>(ListUiState.Loading)
    val uiState: StateFlow<ListUiState> = _uiState.asStateFlow()

    init {
        loadProducts()
    }

    fun loadProducts(forceUpdate: Boolean = false) {
        viewModelScope.launch {
            _uiState.value = ListUiState.Loading
            try {
                // Repository decide se usare Cache o Rete (Offline-First)
                val products = repository.getProducts(forceUpdate)
                _uiState.value = ListUiState.Success(products)
            } catch (e: Exception) {
                _uiState.value = ListUiState.Error(e.message ?: "Errore sconosciuto")
            }
        }
    }

    fun toggleFavorite(product: Product) {
        viewModelScope.launch {
            repository.toggleFavorite(product)
            // Ricarichiamo per aggiornare i cuori (SSOT: la verità è nel DB)
            loadProducts(forceUpdate = false)
        }
    }
}

sealed class ListUiState {
    data object Loading : ListUiState()
    data class Success(val products: List<Product>) : ListUiState()
    data class Error(val message: String) : ListUiState()
}
