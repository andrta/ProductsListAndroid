package com.tamboo.data.repository

import com.tamboo.data.datasource.ProductLocalDataSource
import com.tamboo.data.mapper.toDomain
import com.tamboo.domain.model.Product
import com.tamboo.domain.repository.ProductRepository
import com.tamboo.network.retrofit.FakeStoreApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProductRepositoryImpl(
    private val api: FakeStoreApi,
    private val localDataSource: ProductLocalDataSource
) : ProductRepository {

    override suspend fun getProducts(): List<Product> {
        val remoteProducts = api.getProducts()

        val favoriteIds = localDataSource.getFavoriteIds()

        return remoteProducts.map { dto ->
            dto.toDomain(isFavorite = favoriteIds.contains(dto.id))
        }
    }

    override fun getFavoriteProducts(): Flow<List<Product>> {
        return localDataSource.getFavoriteProductsStream()
            .map { entities ->
                entities.map { it.toDomain() }
            }
    }

    override suspend fun toggleFavorite(product: Product) {
        localDataSource.toggleFavorite(
            id = product.id,
            title = product.title,
            price = product.price,
            image = product.imageUrl,
            description = product.description,
            category = product.category
        )
    }
}
