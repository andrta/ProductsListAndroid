package com.tamboo.data.datasource

import app.cash.turbine.test
import com.tamboo.database.model.ProductEntity
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RealmProductDataSourceTest {

    private lateinit var realm: Realm
    private lateinit var dataSource: RealmProductDataSource

    @Before
    fun setup() {
        // Configura un Realm IN-MEMORY.
        // Poiché questo test gira su un dispositivo Android (Emulatore),
        // userà le librerie native Android corrette.
        val config = RealmConfiguration.Builder(schema = setOf(ProductEntity::class))
            .inMemory()
            .name("test-realm-instrumented")
            .build()

        realm = Realm.open(config)
        dataSource = RealmProductDataSource(realm)
    }

    @After
    fun tearDown() {
        // Chiude il DB e libera la memoria dopo ogni test
        realm.close()
    }

    @Test
    fun toggleFavorite_adds_product_if_it_does_not_exist() = runTest {
        // GIVEN - Il DB è vuoto all'inizio

        // WHEN - Chiamiamo toggle per aggiungere un prodotto che non c'è
        dataSource.toggleFavorite(
            id = 1,
            title = "Zaino Test",
            price = 50.0,
            image = "http://img.com/1",
            description = "Descrizione test",
            category = "Test Category"
        )

        // THEN - Verifichiamo direttamente su Realm che sia stato salvato
        val savedProduct = realm.query<ProductEntity>("id == $0", 1).first().find()

        assertNotNull("Il prodotto dovrebbe essere stato salvato nel DB", savedProduct)
        assertEquals("Zaino Test", savedProduct?.title)
        assertTrue("Il flag isFavorite dovrebbe essere true", savedProduct?.isFavorite == true)
    }

    @Test
    fun toggleFavorite_removes_product_if_it_already_exists() = runTest {
        // GIVEN - Inseriamo manualmente un prodotto nel DB
        realm.write {
            copyToRealm(ProductEntity().apply {
                id = 99
                title = "Da Cancellare"
                price = 10.0
                isFavorite = true
            })
        }

        // Verifica preliminare: il prodotto deve esistere
        val countBefore = realm.query<ProductEntity>("id == $0", 99).count().find()
        assertEquals(1, countBefore)

        // WHEN - Chiamiamo toggle sullo stesso ID (dovrebbe rimuoverlo)
        dataSource.toggleFavorite(
            id = 99,
            title = "Titolo Ignorato", // Non importa, tanto cancella
            price = 0.0,
            image = "",
            description = "",
            category = ""
        )

        // THEN - Verifichiamo che sia stato cancellato
        val deletedProduct = realm.query<ProductEntity>("id == $0", 99).first().find()
        assertNull("Il prodotto dovrebbe essere stato rimosso dal DB", deletedProduct)
    }

    @Test
    fun getFavoriteIds_returns_correct_set_of_IDs() = runTest {
        // GIVEN - Popoliamo il DB con 3 prodotti favoriti
        realm.write {
            copyToRealm(ProductEntity().apply { id = 10; title = "A"; isFavorite = true })
            copyToRealm(ProductEntity().apply { id = 20; title = "B"; isFavorite = true })
            copyToRealm(ProductEntity().apply { id = 30; title = "C"; isFavorite = true })
        }

        // WHEN - Chiediamo gli ID
        val ids = dataSource.getFavoriteIds()

        // THEN - Verifichiamo il set
        assertEquals("Dovrebbero esserci 3 ID", 3, ids.size)
        assertTrue(ids.contains(10))
        assertTrue(ids.contains(20))
        assertTrue(ids.contains(30))
    }

    @Test
    fun getFavoriteProductsStream_emits_updates_reactively() = runTest {
        // GIVEN - Il Flow viene osservato con Turbine
        dataSource.getFavoriteProductsStream().test {

            // 1. Stato iniziale: Lista vuota
            val initialList = awaitItem()
            assertTrue("La lista iniziale dovrebbe essere vuota", initialList.isEmpty())

            // 2. Azione: Aggiungiamo un elemento tramite il DataSource
            dataSource.toggleFavorite(77, "Reattivo", 10.0, "img", "desc", "cat")

            // 3. Verifica: Il Flow deve emettere automaticamente la lista aggiornata
            val updatedList = awaitItem()
            assertEquals("La lista dovrebbe contenere 1 elemento", 1, updatedList.size)
            assertEquals("Reattivo", updatedList[0].title)

            // 4. Azione: Rimuoviamo l'elemento
            dataSource.toggleFavorite(77, "Reattivo", 10.0, "img", "desc", "cat")

            // 5. Verifica: Il Flow deve emettere lista vuota
            val finalList = awaitItem()
            assertTrue("La lista finale dovrebbe essere vuota", finalList.isEmpty())

            cancelAndIgnoreRemainingEvents()
        }
    }
}
