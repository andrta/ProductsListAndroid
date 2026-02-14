package com.tamboo.data.datasource

import kotlinx.coroutines.flow.Flow
import com.tamboo.database.model.ProductEntity
import com.tamboo.network.model.ProductDto

interface ProductLocalDataSource {
    suspend fun getAllProducts(): List<ProductEntity>
    suspend fun getFavoriteIds(): Set<Int>
    fun getFavoriteProductsStream(): Flow<List<ProductEntity>>
    fun getFavoriteIdsStream(): Flow<Set<Int>>
    suspend fun toggleFavorite(id: Int, title: String, price: Double, image: String, description: String, category: String)
    suspend fun isCacheValid(expirationTimeInMillis: Long): Boolean
    suspend fun cacheResponse(dtoList: List<ProductDto>)
}