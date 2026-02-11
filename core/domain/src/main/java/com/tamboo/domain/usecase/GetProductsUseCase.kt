package com.tamboo.domain.usecase

import com.tamboo.domain.model.Product
import com.tamboo.domain.repository.ProductRepository

class GetProductsUseCase(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(forceUpdate: Boolean = false): List<Product> {
        return repository.getProducts(forceUpdate)
    }
}
