package com.tamboo.domain.usecase

import com.tamboo.domain.model.Product
import com.tamboo.domain.repository.ProductRepository
import com.tamboo.testing.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.just
import io.mockk.Runs
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ToggleFavoriteUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: ProductRepository = mockk()

    private lateinit var useCase: ToggleFavoriteUseCase

    @Before
    fun setup() {
        useCase = ToggleFavoriteUseCase(repository)
    }

    @Test
    fun `invoke should call repository toggleFavorite with correct product`() = runTest {
        // GIVEN
        val product = Product(id = 1, title = "Test", price = 10.0, imageUrl = "", description = "", category = "", isFavorite = false)

        coEvery { repository.toggleFavorite(any()) } just Runs

        // WHEN
        useCase(product)

        // THEN
        coVerify(exactly = 1) { repository.toggleFavorite(product) }
    }
}
