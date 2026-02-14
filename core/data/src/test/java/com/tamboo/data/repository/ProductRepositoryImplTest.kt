package com.tamboo.data.repository

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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.IOException

class ProductRepositoryImplTest {
    private val api: FakeStoreApi = mockk()
    private val localDataSource: ProductLocalDataSource = mockk(relaxed = true)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: ProductRepositoryImpl
    private val fakeProductEntity = ProductEntity().apply {
        id = 1
        title = "Test Product"
        price = 10.0
        imageUrl = "url"
        description = "desc"
        category = "cat"
        isFavorite = false
    }

    private val fakeProductDto = ProductDto(
        id = 1,
        title = "Test Product",
        price = 10.0,
        description = "desc",
        category = "cat",
        image = "url",
        rating = RatingDto(4.5, 10)
    )

    @Before
    fun setup() {
        repository = ProductRepositoryImpl(api, localDataSource, testDispatcher)
    }

    @Test
    fun `getProducts returns local data WITHOUT network call when cache is valid and forceUpdate is false`() =
        runTest {
            // GIVEN
            coEvery { localDataSource.isCacheValid(any()) } returns true
            coEvery { localDataSource.getAllProducts() } returns listOf(fakeProductEntity)

            // WHEN
            val result = repository.getProducts(forceUpdate = false)

            // THEN
            coVerify(exactly = 0) { api.getProducts() }
            coVerify(exactly = 1) { localDataSource.getAllProducts() }

            assertEquals(1, result.size)
            assertEquals(fakeProductEntity.title, result[0].title)
        }

    @Test
    fun `getProducts calls network AND saves to cache when cache is invalid`() = runTest {
        // GIVEN
        coEvery { localDataSource.isCacheValid(any()) } returns false
        coEvery { api.getProducts() } returns listOf(fakeProductDto)
        coEvery { localDataSource.getAllProducts() } returns listOf(fakeProductEntity)

        // WHEN
        val result = repository.getProducts(forceUpdate = false)

        // THEN
        coVerify(exactly = 1) { api.getProducts() }
        coVerify(exactly = 1) { localDataSource.cacheResponse(listOf(fakeProductDto)) }
        assertEquals(1, result.size)
    }

    @Test
    fun `getProducts calls network even if cache is valid when forceUpdate is TRUE`() = runTest {
        // GIVEN
        coEvery { localDataSource.isCacheValid(any()) } returns true // Cache valida
        coEvery { api.getProducts() } returns listOf(fakeProductDto)
        coEvery { localDataSource.getAllProducts() } returns listOf(fakeProductEntity)

        // WHEN
        repository.getProducts(forceUpdate = true)

        // THEN
        coVerify(exactly = 1) { api.getProducts() }
    }

    @Test
    fun `getProducts suppresses network error and returns local data if forceUpdate is FALSE`() =
        runTest {
            // GIVEN
            coEvery { localDataSource.isCacheValid(any()) } returns false
            coEvery { api.getProducts() } throws IOException("Network error")
            coEvery { localDataSource.getAllProducts() } returns listOf(fakeProductEntity)

            // WHEN
            val result = repository.getProducts(forceUpdate = false)

            // THEN
            assertEquals(1, result.size)
            coVerify(exactly = 0) { localDataSource.cacheResponse(any()) }
        }

    @Test
    fun `getProducts re-throws network error if forceUpdate is TRUE`() = runTest {
        // GIVEN
        coEvery { localDataSource.isCacheValid(any()) } returns true
        coEvery { api.getProducts() } throws IOException("Network error")

        // WHEN / THEN
        try {
            repository.getProducts(forceUpdate = true)
            throw AssertionError("IOException")
        } catch (e: IOException) {
            assertEquals("Network error", e.message)
        }
    }

    @Test
    fun `getProducts throws Exception when network fails AND local data is empty`() = runTest {
        // GIVEN
        coEvery { localDataSource.isCacheValid(any()) } returns false
        coEvery { api.getProducts() } throws IOException("Network error")
        coEvery { localDataSource.getAllProducts() } returns emptyList()

        // WHEN / THEN
        try {
            repository.getProducts(forceUpdate = false)
            throw AssertionError("Exception thrown")
        } catch (e: Exception) {
            assertEquals("No offline data.", e.message)
        }
    }

    @Test
    fun `toggleFavorite delegates to localDataSource`() = runTest {
        // GIVEN
        val domainProduct = Product(
            id = 1,
            title = "Test",
            price = 10.0,
            description = "desc",
            category = "cat",
            imageUrl = "url",
            isFavorite = false
        )

        // WHEN
        repository.toggleFavorite(domainProduct)

        // THEN
        coVerify(exactly = 1) {
            localDataSource.toggleFavorite(
                id = 1,
                title = "Test",
                price = 10.0,
                image = "url",
                description = "desc",
                category = "cat"
            )
        }
    }

    @Test
    fun `getFavoriteProducts maps entities to domain objects`() = runTest {
        // GIVEN
        val entities = listOf(fakeProductEntity)
        every { localDataSource.getFavoriteProductsStream() } returns flowOf(entities)

        // WHEN
        val resultFlow = repository.getFavoriteProducts()
        val resultList = resultFlow.first()

        // THEN
        assertEquals(1, resultList.size)
        assertEquals(fakeProductEntity.title, resultList[0].title)
    }
}
