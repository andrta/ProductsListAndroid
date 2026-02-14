package com.tamboo.domain.usecase

import app.cash.turbine.test
import com.tamboo.domain.model.Product
import com.tamboo.domain.repository.ProductRepository
import com.tamboo.testing.MainDispatcherRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GetFavoriteProductsUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: ProductRepository = mockk()

    private lateinit var useCase: GetFavoriteProductsUseCase

    @Before
    fun setup() {
        useCase = GetFavoriteProductsUseCase(repository)
    }

    @Test
    fun `invoke should call repository getFavoriteProducts`() = runTest {
        // GIVEN
        val expected = listOf(Product(1, "Test", 10.0, "", "", "", true))

        every { repository.getFavoriteProducts() } returns flowOf(expected)

        // WHEN & THEN
        useCase().test {
            val result = awaitItem()
            assertEquals(expected, result)
            awaitComplete()
        }
        verify(exactly = 1) { repository.getFavoriteProducts() }
    }
}
