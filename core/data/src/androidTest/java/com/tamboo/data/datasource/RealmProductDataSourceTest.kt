package com.tamboo.data.datasource

import com.tamboo.database.model.ProductEntity
import com.tamboo.network.model.ProductDto
import com.tamboo.network.model.RatingDto
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RealmProductDataSourceTest {

    private lateinit var realm: Realm
    private lateinit var dataSource: RealmProductDataSource

    @Before
    fun setup() {
        val config = RealmConfiguration.Builder(schema = setOf(ProductEntity::class))
            .inMemory()
            .name("test-realm-instrumented")
            .build()
        realm = Realm.open(config)
        dataSource = RealmProductDataSource(realm)
    }

    @After
    fun tearDown() {
        realm.close()
    }

    @Test
    fun cacheResponse_saves_data_and_preserves_isFavorite_status() = runTest {
        // GIVEN - Un prodotto esiste già nel DB ed è PREFERITO
        realm.write {
            copyToRealm(ProductEntity().apply {
                id = 99
                title = "Titolo Vecchio"
                isFavorite = true // Utente l'ha messo favorito
            })
        }

        // DTO che arriva dalla rete (stesso ID, titolo aggiornato, ma non sa nulla dei preferiti)
        val networkData = listOf(
            ProductDto(
                id = 99,
                title = "Titolo Aggiornato",
                price = 20.0,
                description = "",
                category = "",
                image = "",
                rating = RatingDto(0.0, 0)
            )
        )

        // WHEN - Aggiorniamo la cache
        dataSource.cacheResponse(networkData)

        // THEN
        val updatedProduct = realm.query<ProductEntity>("id == $0", 99).first().find()
        assertNotNull(updatedProduct)

        // 1. I dati devono essere aggiornati
        assertEquals("Titolo Aggiornato", updatedProduct?.title)

        // 2. MA il flag isFavorite deve essere rimasto TRUE (preservato)
        assertTrue(
            "Il flag isFavorite deve essere preservato dopo update API",
            updatedProduct?.isFavorite == true
        )
    }

    @Test
    fun toggleFavorite_toggles_flag_instead_of_deleting() = runTest {
        // GIVEN - Prodotto esistente e preferito
        realm.write {
            copyToRealm(ProductEntity().apply {
                id = 10
                title = "Test"
                isFavorite = true
            })
        }

        // WHEN - Togliamo il favorito
        dataSource.toggleFavorite(10, "", 0.0, "", "", "")

        // THEN
        val product = realm.query<ProductEntity>("id == $0", 10).first().find()

        // NON deve essere null (non cancellato)
        assertNotNull(product)
        // Deve essere false
        assertFalse("Il flag dovrebbe essere diventato false", product?.isFavorite == true)

        // WHEN - Rimettiamo il favorito
        dataSource.toggleFavorite(10, "", 0.0, "", "", "")

        // THEN
        val productAgain = realm.query<ProductEntity>("id == $0", 10).first().find()
        assertTrue("Il flag dovrebbe essere tornato true", productAgain?.isFavorite == true)
    }

    @Test
    fun getAllProducts_returns_everything() = runTest {
        realm.write {
            copyToRealm(ProductEntity().apply { id = 1 })
            copyToRealm(ProductEntity().apply { id = 2 })
        }

        val list = dataSource.getAllProducts()
        assertEquals(2, list.size)
    }
}
