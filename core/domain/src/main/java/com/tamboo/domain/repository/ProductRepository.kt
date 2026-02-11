package com.tamboo.domain.repository


import com.tamboo.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    suspend fun getProducts(forceUpdate: Boolean = false): List<Product>
    fun getFavoriteProducts(): Flow<List<Product>>
    suspend fun toggleFavorite(product: Product)
}
