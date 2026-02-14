package com.tamboo.data.repository

import com.tamboo.data.datasource.ProductLocalDataSource
import com.tamboo.data.mapper.toDomain
import com.tamboo.domain.model.Product
import com.tamboo.domain.repository.ProductRepository
import com.tamboo.network.service.FakeStoreApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit

class ProductRepositoryImpl(
    private val api: FakeStoreApi,
    private val localDataSource: ProductLocalDataSource
) : ProductRepository {
    // Definiamo la durata della cache (es. 1 Ora)
    private val CACHE_TIMEOUT = TimeUnit.HOURS.toMillis(1)

    // Aggiungiamo il parametro forceUpdate (default false)
    override suspend fun getProducts(forceUpdate: Boolean): List<Product> {

        // 1. Controlliamo se la cache è valida
        val isCacheValid = localDataSource.isCacheValid(CACHE_TIMEOUT)

        // 2. Logica di aggiornamento:
        // Scarichiamo se:
        // - L'utente ha forzato l'update (Pull-to-refresh)
        // - OPPURE la cache è scaduta/vuota
        val shouldFetchFromNetwork = forceUpdate || !isCacheValid

        if (shouldFetchFromNetwork) {
            try {
                val remoteProducts = api.getProducts()
                localDataSource.cacheResponse(remoteProducts) // Questo aggiorna anche lastUpdated
            } catch (e: Exception) {
                // Se la rete fallisce:
                // - Se era un forceUpdate, potremmo voler lanciare errore alla UI
                // - Se era un update automatico, silenziamo l'errore e usiamo la cache vecchia
                if (forceUpdate) throw e
                e.printStackTrace()
            }
        }

        // 3. Ritorniamo SEMPRE dal DB (SSOT)
        val localProducts = localDataSource.getAllProducts()

        if (localProducts.isEmpty() && shouldFetchFromNetwork) {
            // Se il DB è vuoto e abbiamo provato a scaricare (fallendo), lanciamo errore
            throw Exception("Dati non disponibili offline.")
        }

        return localProducts.map { it.toDomain() }
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
