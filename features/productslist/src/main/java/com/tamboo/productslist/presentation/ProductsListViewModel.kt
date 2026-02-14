package com.tamboo.productslist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tamboo.domain.model.Product
import com.tamboo.domain.repository.ProductRepository
import com.tamboo.domain.usecase.GetProductsUseCase
import com.tamboo.domain.usecase.ToggleFavoriteUseCase
import com.tamboo.productslist.uistate.ProductsListUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductsListViewModel(
    private val getProductsUseCase: GetProductsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProductsListUiState>(ProductsListUiState.Loading)
    val uiState: StateFlow<ProductsListUiState> = _uiState.asStateFlow()

    init {
        loadProducts()
    }
    fun loadProducts(forceUpdate: Boolean = false) {
        viewModelScope.launch {
            _uiState.value = ProductsListUiState.Loading
            try {
                val products = getProductsUseCase(forceUpdate)
                _uiState.value = ProductsListUiState.Success(products)
            } catch (e: Exception) {
                _uiState.value = ProductsListUiState.Error(e.message ?: "Error")
            }
        }
    }

    fun toggleFavorite(product: Product) {
        viewModelScope.launch {
            toggleFavoriteUseCase(product)
            loadProducts(forceUpdate = false)
        }
    }
}
