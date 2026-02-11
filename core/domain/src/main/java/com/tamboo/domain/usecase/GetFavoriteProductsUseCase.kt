package com.tamboo.domain.usecase

import com.tamboo.domain.model.Product
import com.tamboo.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow

class GetFavoriteProductsUseCase(
    private val repository: ProductRepository
) {
    operator fun invoke(): Flow<List<Product>> {
        return repository.getFavoriteProducts()
    }
}
