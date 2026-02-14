package com.tamboo.data.repository

import app.cash.turbine.test
import com.tamboo.data.datasource.ProductLocalDataSource
import com.tamboo.database.model.ProductEntity
import com.tamboo.domain.model.Product
import com.tamboo.network.model.ProductDto
import com.tamboo.network.model.RatingDto
import com.tamboo.network.service.FakeStoreApi
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ProductRepositoryImplTest {

    // 1. Mock delle dipendenze
    private val api: FakeStoreApi = mockk()

    // relaxed = true è importante: evita di dover stubbare metodi che non ritornano nulla (Unit)
    // come cacheResponse o toggleFavorite, se non ci interessa il loro risultato specifico.
    private val localDataSource: ProductLocalDataSource = mockk(relaxed = true)

    // 2. System Under Test
    private lateinit var repository: ProductRepositoryImpl

    @Before
    fun setup() {
        repository = ProductRepositoryImpl(api, localDataSource)
    }

    @Test
    fun `getProducts refreshes cache from API and returns data from DB (SSOT)`() = runTest {
        // GIVEN
        val remoteData = listOf(createProductDto(1, "Titolo Remoto"))

        // Simuliamo che nel DB ci sia il dato (magari con isFavorite=true)
        val localData = listOf(
            ProductEntity().apply {
                id = 1
                title = "Titolo Remoto"
                isFavorite = true // Il DB è la fonte di verità per i favoriti
            }
        )

        // Stubbing
        coEvery { api.getProducts() } returns remoteData
        coEvery { localDataSource.getAllProducts() } returns localData // Il repo deve leggere da qui

        // WHEN
        val result = repository.getProducts()

        // THEN
        // 1. Verifica comportamento SSOT: Il dato finale deve avere le proprietà del DB (es. isFavorite=true)
        assertEquals(1, result.size)
        assertEquals("Titolo Remoto", result[0].title)
        assertTrue("Il flag isFavorite deve provenire dal DB locale", result[0].isFavorite)

        // 2. Verifica interazioni:
        // Deve aver chiamato l'API
        coVerify(exactly = 1) { api.getProducts() }
        // Deve aver salvato i dati nella cache
        coVerify(exactly = 1) { localDataSource.cacheResponse(remoteData) }
        // Deve aver letto i dati dal DB per restituirli
        coVerify(exactly = 1) { localDataSource.getAllProducts() }
    }

    @Test
    fun `getProducts returns local data when API fails (Offline Support)`() = runTest {
        // GIVEN
        // L'API fallisce (es. Nessuna connessione)
        coEvery { api.getProducts() } throws Exception("Network Error")

        // Ma abbiamo dati vecchi nel DB
        val oldLocalData = listOf(
            ProductEntity().apply { id = 1; title = "Vecchio Titolo"; isFavorite = false }
        )
        coEvery { localDataSource.getAllProducts() } returns oldLocalData

        // WHEN
        val result = repository.getProducts()

        // THEN
        // Non deve lanciare eccezioni, ma restituire i dati locali
        assertEquals(1, result.size)
        assertEquals("Vecchio Titolo", result[0].title)

        // Verifichiamo che NON abbia provato a salvare nulla (visto che l'API è fallita)
        coVerify(exactly = 0) { localDataSource.cacheResponse(any()) }
    }

    @Test
    fun `getFavoriteProducts emits mapped domain objects`() = runTest {
        // GIVEN - Una lista di entity dal DB
        val dbEntities = listOf(
            ProductEntity().apply {
                id = 10
                title = "Giacca"
                price = 100.0
                isFavorite = true
            }
        )

        // Stubbing: il DataSource restituisce un Flow di questa lista
        every { localDataSource.getFavoriteProductsStream() } returns flowOf(dbEntities)

        // WHEN & THEN - Testiamo il Flow con Turbine
        repository.getFavoriteProducts().test {
            val item = awaitItem() // Aspettiamo la prima emissione

            assertEquals(1, item.size)
            assertEquals(10, item[0].id)
            assertEquals("Giacca", item[0].title)
            assertTrue(item[0].isFavorite)

            awaitComplete()
        }
    }

    @Test
    fun `toggleFavorite calls datasource correctly`() = runTest {
        // GIVEN
        val product = Product(
            id = 5,
            title = "Cappello",
            price = 15.0,
            description = "Bello",
            category = "Accessori",
            imageUrl = "img_url",
            isFavorite = false
        )

        // WHEN
        repository.toggleFavorite(product)

        // THEN
        // Verifichiamo solo che il Repository passi correttamente i parametri al DataSource
        coVerify(exactly = 1) {
            localDataSource.toggleFavorite(
                id = 5,
                title = "Cappello",
                price = 15.0,
                image = "img_url",
                description = "Bello",
                category = "Accessori"
            )
        }
    }

    // --- Helper Methods ---
    private fun createProductDto(id: Int, title: String) = ProductDto(
        id = id,
        title = title,
        price = 10.0,
        description = "Desc",
        category = "Cat",
        image = "url",
        rating = RatingDto(4.5, 10)
    )
}
