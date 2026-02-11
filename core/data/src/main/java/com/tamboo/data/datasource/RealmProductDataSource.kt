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

    // NUOVO: Restituisce TUTTI i prodotti (per la lista principale)
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

    // MODIFICATO: Ora non cancella più l'oggetto, cambia solo il flag
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
                // Se esiste, invertiamo il flag e basta. Non cancelliamo!
                existingProduct.isFavorite = !existingProduct.isFavorite
            } else {
                // Edge case: Se l'utente clicca favorito su un prodotto che
                // per qualche motivo non è ancora nel DB (es. errore cache precedente)
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
        // 1. Cerchiamo un prodotto qualsiasi (o il più recente/vecchio a seconda della logica)
        // Qui prendiamo il primo che troviamo per semplicità.
        val firstProduct = realm.query<ProductEntity>().first().find() ?: return false // Se vuoto, cache non valida

        // 2. Calcoliamo quanto tempo è passato
        val currentTime = System.currentTimeMillis()
        val lastUpdate = firstProduct.lastUpdated

        // 3. È valido se la differenza è MINORE del tempo di scadenza
        return (currentTime - lastUpdate) < expirationTimeInMillis
    }

    override suspend fun cacheResponse(dtoList: List<ProductDto>) {
        val now = System.currentTimeMillis() // Timestamp attuale per tutti i prodotti

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
                    lastUpdated = now // <--- AGGIORNIAMO IL TIMESTAMP
                }, updatePolicy = UpdatePolicy.ALL)
            }
        }
    }
}
