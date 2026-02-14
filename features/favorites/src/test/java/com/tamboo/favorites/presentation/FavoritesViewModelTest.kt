package com.tamboo.favorites.presentation

import app.cash.turbine.test
import com.tamboo.domain.model.Product
import com.tamboo.domain.usecase.GetFavoriteProductsUseCase
import com.tamboo.domain.usecase.ToggleFavoriteUseCase
import com.tamboo.testing.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class FavoritesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getFavoritesUseCase: GetFavoriteProductsUseCase = mockk()
    private val toggleUseCase: ToggleFavoriteUseCase = mockk()

    @Test
    fun `favoritesState reflects emissions from UseCase`() = runTest {
        val mockFavorites = listOf(Product(1, "Fav", 10.0, "", "", "", true))
        every { getFavoritesUseCase() } returns flowOf(mockFavorites)

        val viewModel = FavoritesViewModel(getFavoritesUseCase, toggleUseCase)

        viewModel.favoritesState.test {
            assertEquals(mockFavorites, awaitItem())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onRemoveFavorite calls toggle UseCase`() = runTest {
        // GIVEN
        val product = Product(1, "Fav", 10.0, "", "", "", true)
        every { getFavoritesUseCase() } returns flowOf(listOf(product))
        coEvery { toggleUseCase(any()) } returns Unit

        val viewModel = FavoritesViewModel(getFavoritesUseCase, toggleUseCase)

        // WHEN
        viewModel.onRemoveFavorite(product)
        advanceUntilIdle()

        // THEN
        coVerify(exactly = 1) { toggleUseCase(product) }
    }
}
