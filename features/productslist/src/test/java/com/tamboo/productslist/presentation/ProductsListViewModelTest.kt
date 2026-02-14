package com.tamboo.productslist.presentation

import app.cash.turbine.test
import com.tamboo.domain.model.Product
import com.tamboo.domain.usecase.GetProductsUseCase
import com.tamboo.domain.usecase.ObserveFavoriteIdsUseCase
import com.tamboo.domain.usecase.ToggleFavoriteUseCase
import com.tamboo.productslist.uistate.ProductsListUiState
import com.tamboo.testing.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ProductsListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(StandardTestDispatcher())

    private val getProductsUseCase: GetProductsUseCase = mockk()
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase = mockk(relaxed = true)
    private val observeFavoriteIdsUseCase: ObserveFavoriteIdsUseCase = mockk()

    private lateinit var viewModel: ProductsListViewModel

    @Test
    fun `loadProducts updates state to Success`() = runTest {
        // GIVEN
        val mockProducts = listOf(Product(1, "Test", 10.0, "", "", "", false))

        coEvery { getProductsUseCase(any()) } returns mockProducts
        every { observeFavoriteIdsUseCase() } returns flowOf(emptySet())

        // WHEN
        viewModel = ProductsListViewModel(
            getProductsUseCase,
            toggleFavoriteUseCase,
            observeFavoriteIdsUseCase
        )

        // THEN
        viewModel.uiState.test {
            val firstItem = awaitItem()
            if (firstItem is ProductsListUiState.Loading) {
                val success = awaitItem() as ProductsListUiState.Success
                assertEquals(mockProducts, success.products)
            } else {
                val success = firstItem as ProductsListUiState.Success
                assertEquals(mockProducts, success.products)
            }
        }
    }

    @Test
    fun `uiState updates isFavorite flag when observeFavoriteIds emits new values`() = runTest {
        // GIVEN
        val product = Product(1, "Test", 10.0, "", "", "", isFavorite = false)
        val productsList = listOf(product)
        coEvery { getProductsUseCase(any()) } returns productsList

        val favoritesFlow = MutableSharedFlow<Set<Int>>(replay = 1)
        every { observeFavoriteIdsUseCase() } returns favoritesFlow
        favoritesFlow.emit(emptySet())

        // WHEN
        viewModel = ProductsListViewModel(
            getProductsUseCase,
            toggleFavoriteUseCase,
            observeFavoriteIdsUseCase
        )

        // THEN
        viewModel.uiState.test {
            val firstEmission = awaitItem()
            if (firstEmission is ProductsListUiState.Loading) {
                awaitItem()
            }

            favoritesFlow.emit(setOf(1))

            val successState = awaitItem() as ProductsListUiState.Success
            assertEquals(true, successState.products[0].isFavorite)
        }
    }
}
