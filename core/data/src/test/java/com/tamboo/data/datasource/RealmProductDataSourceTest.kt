package com.tamboo.data.datasource

import app.cash.turbine.test
import com.tamboo.database.model.ProductEntity
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class RealmProductDataSourceTest {

    private lateinit var realm: Realm
    private lateinit var dataSource: RealmProductDataSource

    @Before
    fun setup() {
        val config = RealmConfiguration.Builder(schema = setOf(ProductEntity::class))
            .inMemory()
            .name("test-realm")
            .build()

        realm = Realm.open(config)
        dataSource = RealmProductDataSource(realm)
    }

    @After
    fun tearDown() {
        realm.close()
    }

    @Test
    fun `toggleFavorite adds product if it does not exist`() = runTest {
        // GIVEN - Il DB è vuoto

        // WHEN - Chiamiamo toggle per aggiungere
        dataSource.toggleFavorite(
            id = 1,
            title = "Test Product",
            price = 10.0,
            image = "img",
            description = "desc",
            category = "cat"
        )

        // THEN - Verifichiamo direttamente su Realm che sia stato salvato
        val savedProduct = realm.query<ProductEntity>("id == $0", 1).first().find()
        assertTrue("Il prodotto dovrebbe esistere nel DB", savedProduct != null)
        assertEquals("Test Product", savedProduct?.title)
        assertTrue(savedProduct?.isFavorite == true)
    }

    @Test
    fun `toggleFavorite removes product if it already exists`() = runTest {
        // GIVEN - Inseriamo manualmente un prodotto nel DB
        realm.write {
            copyToRealm(ProductEntity().apply {
                id = 1
                title = "Da Cancellare"
                isFavorite = true
            })
        }

        // Assicuriamoci che ci sia
        var count = realm.query<ProductEntity>().count().find()
        assertEquals(1, count)

        // WHEN - Chiamiamo toggle (che dovrebbe rimuovere)
        dataSource.toggleFavorite(1, "", 0.0, "", "", "")

        // THEN - Verifichiamo che sia stato cancellato
        val deletedProduct = realm.query<ProductEntity>("id == $0", 1).first().find()
        assertTrue("Il prodotto dovrebbe essere stato rimosso", deletedProduct == null)

        count = realm.query<ProductEntity>().count().find()
        assertEquals(0, count)
    }

    @Test
    fun `getFavoriteIds returns correct set of IDs`() = runTest {
        // GIVEN - Popoliamo il DB con 3 prodotti
        realm.write {
            copyToRealm(ProductEntity().apply { id = 10; title = "A"; isFavorite = true })
            copyToRealm(ProductEntity().apply { id = 20; title = "B"; isFavorite = true })
            copyToRealm(ProductEntity().apply { id = 30; title = "C"; isFavorite = true })
        }

        // WHEN
        val ids = dataSource.getFavoriteIds()

        // THEN
        assertEquals(3, ids.size)
        assertTrue(ids.contains(10))
        assertTrue(ids.contains(20))
        assertTrue(ids.contains(30))
    }

    @Test
    fun `getFavoriteProductsStream emits updates reactively`() = runTest {
        // Usiamo Turbine per testare il Flow
        dataSource.getFavoriteProductsStream().test {

            // 1. Stato iniziale: Lista vuota
            val initialEmission = awaitItem()
            assertTrue(initialEmission.isEmpty())

            // 2. Azione: Aggiungiamo un elemento
            dataSource.toggleFavorite(1, "Nuovo", 10.0, "img", "desc", "cat")

            // 3. Verifica: Il Flow deve emettere la lista con 1 elemento
            val secondEmission = awaitItem()
            assertEquals(1, secondEmission.size)
            assertEquals("Nuovo", secondEmission[0].title)

            // 4. Azione: Rimuoviamo l'elemento
            dataSource.toggleFavorite(1, "Nuovo", 10.0, "img", "desc", "cat")

            // 5. Verifica: Il Flow deve emettere lista vuota
            val thirdEmission = awaitItem()
            assertTrue(thirdEmission.isEmpty())

            cancelAndIgnoreRemainingEvents()
        }
    }
}
