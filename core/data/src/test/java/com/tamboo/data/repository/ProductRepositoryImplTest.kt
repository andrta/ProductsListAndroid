package com.tamboo.data.repository

import app.cash.turbine.test
import com.tamboo.data.datasource.ProductLocalDataSource
import com.tamboo.database.model.ProductEntity
import com.tamboo.domain.model.Product
import com.tamboo.network.model.ProductDto
import com.tamboo.network.model.RatingDto
import com.tamboo.network.retrofit.FakeStoreApi
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ProductRepositoryTest {

    // 1. Mock delle dipendenze
    private val api: FakeStoreApi = mockk()
    private val localDataSource: ProductLocalDataSource = mockk(relaxed = true)
    // 'relaxed = true' permette di chiamare metodi void senza stubbarli (utile per toggleFavorite)

    // 2. System Under Test
    private lateinit var repository: ProductRepositoryImpl

    @Before
    fun setup() {
        repository = ProductRepositoryImpl(api, localDataSource)
    }

    @Test
    fun `getProducts combines API data with Local favorites correctly`() = runTest {
        // GIVEN - Dati di prova
        val remoteProducts = listOf(
            createProductDto(id = 1, title = "Zaino"),
            createProductDto(id = 2, title = "Maglietta")
        )
        // Simuliamo che l'ID 1 sia tra i favoriti nel DB locale
        val favoriteIds = setOf(1)

        // Stubbing (Configurazione Mock)
        coEvery { api.getProducts() } returns remoteProducts
        coEvery { localDataSource.getFavoriteIds() } returns favoriteIds

        // WHEN - Esecuzione
        val result = repository.getProducts()

        // THEN - Verifica
        assertEquals(2, result.size)

        // Il prodotto 1 deve avere isFavorite = true
        assertEquals(1, result[0].id)
        assertTrue("Il prodotto 1 dovrebbe essere favorito", result[0].isFavorite)

        // Il prodotto 2 deve avere isFavorite = false
        assertEquals(2, result[1].id)
        assertFalse("Il prodotto 2 NON dovrebbe essere favorito", result[1].isFavorite)

        // Verifica interazioni
        coVerify(exactly = 1) { api.getProducts() }
        coVerify(exactly = 1) { localDataSource.getFavoriteIds() }
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

            awaitComplete() // Il flowOf termina subito
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

    @Test(expected = Exception::class)
    fun `getProducts throws exception when API fails`() = runTest {
        // GIVEN
        coEvery { api.getProducts() } throws Exception("Network Error")

        // WHEN
        repository.getProducts()
    }

    // --- Helper ---
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
