package com.tamboo.favorites.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tamboo.domain.model.Product
import com.tamboo.domain.usecase.GetFavoriteProductsUseCase
import com.tamboo.domain.usecase.ToggleFavoriteUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val getFavoriteProductsUseCase: GetFavoriteProductsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    val favoritesState: StateFlow<List<Product>> = getFavoriteProductsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onRemoveFavorite(product: Product) {
        viewModelScope.launch {
            toggleFavoriteUseCase(product)
        }
    }
}
