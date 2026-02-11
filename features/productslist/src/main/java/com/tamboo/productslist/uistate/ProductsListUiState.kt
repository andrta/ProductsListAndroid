package com.tamboo.productslist.uistate

import com.tamboo.domain.model.Product

sealed class ProductsListUiState {
    data object Loading : ProductsListUiState()
    data class Success(val products: List<Product>) : ProductsListUiState()
    data class Error(val message: String) : ProductsListUiState()
}
