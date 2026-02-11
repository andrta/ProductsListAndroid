package com.tamboo.domain.usecase

import com.tamboo.domain.model.Product
import com.tamboo.domain.repository.ProductRepository

class ToggleFavoriteUseCase(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(product: Product) {
        repository.toggleFavorite(product)
    }
}
