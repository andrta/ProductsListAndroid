package com.tamboo.productslist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tamboo.domain.model.Product
import com.tamboo.domain.usecase.GetProductsUseCase
import com.tamboo.domain.usecase.ObserveFavoriteIdsUseCase
import com.tamboo.domain.usecase.ToggleFavoriteUseCase
import com.tamboo.productslist.uistate.ProductsListUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch


class ProductsListViewModel(
    private val getProductsUseCase: GetProductsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val observeFavoriteIdsUseCase: ObserveFavoriteIdsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProductsListUiState>(ProductsListUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadProducts()
    }

    fun loadProducts(forceUpdate: Boolean = false) {
        viewModelScope.launch {
            _uiState.value = ProductsListUiState.Loading

            try {
                val products = getProductsUseCase(forceUpdate)
                val productsFlow = flowOf(products)
                val favoritesFlow = observeFavoriteIdsUseCase()

                combine(productsFlow, favoritesFlow) { productList, favoriteIds ->
                    productList.map { product ->
                        product.copy(isFavorite = favoriteIds.contains(product.id))
                    }
                }.collect { updatedList ->
                    _uiState.value = ProductsListUiState.Success(updatedList)
                }
            } catch (e: Exception) {
                _uiState.value = ProductsListUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun toggleFavorite(product: Product) {
        viewModelScope.launch {
            toggleFavoriteUseCase(product)
        }
    }
}
