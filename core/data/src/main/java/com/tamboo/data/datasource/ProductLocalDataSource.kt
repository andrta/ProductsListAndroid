package com.tamboo.data.datasource

import kotlinx.coroutines.flow.Flow
import com.tamboo.database.model.ProductEntity
import com.tamboo.network.model.ProductDto

interface ProductLocalDataSource {
    suspend fun getFavoriteIds(): Set<Int>
    fun getFavoriteProductsStream(): Flow<List<ProductEntity>>
    suspend fun toggleFavorite(id: Int, title: String, price: Double, image: String, description: String, category: String)

    // Nuovi metodi
    suspend fun getAllProducts(): List<ProductEntity>

    // Controlla se esiste almeno un dato e se è stato aggiornato recentemente
    suspend fun isCacheValid(expirationTimeInMillis: Long): Boolean

    // Aggiorna la cache impostando il timestamp corrente
    suspend fun cacheResponse(dtoList: List<ProductDto>)
}
