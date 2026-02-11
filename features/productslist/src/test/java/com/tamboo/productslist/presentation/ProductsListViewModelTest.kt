package com.tamboo.productslist.presentation

import app.cash.turbine.test
import com.tamboo.domain.model.Product
import com.tamboo.domain.usecase.GetProductsUseCase
import com.tamboo.domain.usecase.ToggleFavoriteUseCase
import com.tamboo.productslist.uistate.ProductsListUiState
import com.tamboo.testing.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ProductsListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(StandardTestDispatcher())

    private val getProductsUseCase: GetProductsUseCase = mockk()
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase = mockk()
    private lateinit var viewModel: ProductsListViewModel

    @Test
    fun `loadProducts updates state to Success`() = runTest {
        val mockProducts = listOf(Product(1, "Test", 10.0, "", "", "", false))
        coEvery { getProductsUseCase(any()) } returns mockProducts

        viewModel = ProductsListViewModel(getProductsUseCase, toggleFavoriteUseCase)

        viewModel.uiState.test {
            assertEquals(ProductsListUiState.Loading, awaitItem())
            val success = awaitItem() as ProductsListUiState.Success
            assertEquals(mockProducts, success.products)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `toggleFavorite calls usecase and reloads products`() = runTest {
        // GIVEN
        val product = Product(1, "Test", 10.0, "", "", "", false)

        coEvery { getProductsUseCase(any()) } returns listOf(product)
        coEvery { toggleFavoriteUseCase(any()) } returns Unit

        viewModel = ProductsListViewModel(getProductsUseCase, toggleFavoriteUseCase)

        // Puliamo la coda delle coroutine dell'init
        advanceUntilIdle()

        // WHEN
        viewModel.toggleFavorite(product)

        // Forza l'esecuzione della coroutine lanciata da toggleFavorite
        advanceUntilIdle()

        // THEN
        // 1. Verifica che l'usecase di toggle sia stato chiamato con il prodotto corretto
        coVerify(exactly = 1) { toggleFavoriteUseCase(product) }

        // 2. Verifica che i prodotti siano stati ricaricati (1 volta init + 1 volta dopo toggle)
        coVerify(exactly = 2) { getProductsUseCase(any()) }
    }
}
