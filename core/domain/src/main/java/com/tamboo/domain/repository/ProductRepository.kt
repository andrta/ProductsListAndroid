package com.tamboo.domain.repository


import com.tamboo.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    suspend fun getProducts(): List<Product>
    fun getFavoriteProducts(): Flow<List<Product>>
    suspend fun toggleFavorite(product: Product)
}
