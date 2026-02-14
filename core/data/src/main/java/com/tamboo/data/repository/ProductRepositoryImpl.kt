package com.tamboo.data.repository

import com.tamboo.data.datasource.ProductLocalDataSource
import com.tamboo.data.mapper.toDomain
import com.tamboo.domain.model.Product
import com.tamboo.domain.repository.ProductRepository
import com.tamboo.network.service.FakeStoreApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class ProductRepositoryImpl(
    private val api: FakeStoreApi,
    private val localDataSource: ProductLocalDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ProductRepository {
    private val CACHE_TIMEOUT = TimeUnit.HOURS.toMillis(1)

    override suspend fun getProducts(forceUpdate: Boolean): List<Product> =
        withContext(ioDispatcher) {
            val isCacheValid = localDataSource.isCacheValid(CACHE_TIMEOUT)
            val shouldFetchFromNetwork = forceUpdate || !isCacheValid

            if (shouldFetchFromNetwork) {
                try {
                    val remoteProducts = api.getProducts()
                    localDataSource.cacheResponse(remoteProducts)
                } catch (e: Exception) {
                    if (forceUpdate) throw e
                    e.printStackTrace()
                }
            }

            val localProducts = localDataSource.getAllProducts()

            if (localProducts.isEmpty() && shouldFetchFromNetwork) {
                throw Exception("No offline data.")
            }

            localProducts.map { it.toDomain() }
        }

    override fun getFavoriteProducts(): Flow<List<Product>> {
        return localDataSource.getFavoriteProductsStream()
            .map { entities ->
                entities.map { it.toDomain() }
            }
            .flowOn(ioDispatcher)
    }

    override suspend fun toggleFavorite(product: Product) = withContext(ioDispatcher) {
        localDataSource.toggleFavorite(
            id = product.id,
            title = product.title,
            price = product.price,
            image = product.imageUrl,
            description = product.description,
            category = product.category
        )
    }

    override fun observeFavoriteIds(): Flow<Set<Int>> {
        return localDataSource.getFavoriteIdsStream()
            .flowOn(ioDispatcher)
    }
}
