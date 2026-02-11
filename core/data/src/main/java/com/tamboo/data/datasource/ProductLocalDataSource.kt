package com.tamboo.data.datasource

import kotlinx.coroutines.flow.Flow
import com.tamboo.database.model.ProductEntity

interface ProductLocalDataSource {
    suspend fun getFavoriteIds(): Set<Int>

    fun getFavoriteProductsStream(): Flow<List<ProductEntity>>

    suspend fun toggleFavorite(
        id: Int,
        title: String,
        price: Double,
        image: String,
        description: String,
        category: String
    )
}
