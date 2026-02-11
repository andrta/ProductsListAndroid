package com.tamboo.data.datasource

import com.tamboo.database.model.ProductEntity
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RealmProductDataSource(
    private val realm: Realm
) : ProductLocalDataSource {

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
                delete(existingProduct)
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
}
