package com.tamboo.data.datasource

import com.tamboo.database.model.ProductEntity
import com.tamboo.network.model.ProductDto
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RealmProductDataSource(
    private val realm: Realm
) : ProductLocalDataSource {
    override suspend fun getAllProducts(): List<ProductEntity> {
        return realm.query<ProductEntity>().find()
    }

    override suspend fun getFavoriteIds(): Set<Int> {
        return realm.query<ProductEntity>("isFavorite == $0", true)
            .find()
            .map { it.id }
            .toSet()
    }

    override fun getFavoriteProductsStream(): Flow<List<ProductEntity>> {
        return realm.query<ProductEntity>("isFavorite == $0", true)
            .asFlow()
            .map { it.list }
    }

    override suspend fun toggleFavorite(
        id: Int,
        title: String,
        price: Double,
        image: String,
        description: String,
        category: String
    ) {
        realm.write {
            val existingProduct = query<ProductEntity>("id == $0", id).first().find()

            if (existingProduct != null) {
                existingProduct.isFavorite = !existingProduct.isFavorite
            } else {
                copyToRealm(ProductEntity().apply {
                    this.id = id
                    this.title = title
                    this.price = price
                    this.imageUrl = image
                    this.description = description
                    this.category = category
                    this.isFavorite = true
                })
            }
        }
    }

    override suspend fun isCacheValid(expirationTimeInMillis: Long): Boolean {
        val firstProduct = realm.query<ProductEntity>().first().find() ?: return false

        val currentTime = System.currentTimeMillis()
        val lastUpdate = firstProduct.lastUpdated

        return (currentTime - lastUpdate) < expirationTimeInMillis
    }

    override suspend fun cacheResponse(dtoList: List<ProductDto>) {
        val now = System.currentTimeMillis()

        realm.write {
            dtoList.forEach { dto ->
                val existing = query<ProductEntity>("id == $0", dto.id).first().find()
                val wasFavorite = existing?.isFavorite ?: false

                copyToRealm(ProductEntity().apply {
                    id = dto.id
                    title = dto.title
                    price = dto.price
                    description = dto.description
                    category = dto.category
                    imageUrl = dto.image
                    isFavorite = wasFavorite
                    lastUpdated = now
                }, updatePolicy = UpdatePolicy.ALL)
            }
        }
    }
}
