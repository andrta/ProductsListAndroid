package com.tamboo.productslist.presentation

import app.cash.turbine.test
import com.tamboo.domain.model.Product
import com.tamboo.domain.repository.ProductRepository
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

    private val repository: ProductRepository = mockk(relaxed = true)
    private lateinit var viewModel: ProductsListViewModel

    @Test
    fun `init loads products and updates state to Success`() = runTest {
        // GIVEN
        val mockProducts = listOf(Product(1, "Test", 10.0, "", "desc", "cat", false))
        coEvery { repository.getProducts(any()) } returns mockProducts

        // WHEN - Inizializziamo il ViewModel
        viewModel = ProductsListViewModel(repository)

        // THEN - Usiamo Turbine per osservare gli stati
        viewModel.uiState.test {
            // Primo stato è Loading (valore iniziale)
            assertEquals(ListUiState.Loading, awaitItem())

            // Secondo stato è Success con i dati
            val successState = awaitItem() as ListUiState.Success
            assertEquals(1, successState.products.size)
            assertEquals("Test", successState.products[0].title)
        }
    }

    @Test
    fun `loadProducts emits Error state when repository fails`() = runTest {
        // GIVEN
        coEvery { repository.getProducts(any()) } throws Exception("Network Error")

        // WHEN
        viewModel = ProductsListViewModel(repository)

        // THEN
        viewModel.uiState.test {
            assertEquals(ListUiState.Loading, awaitItem())

            val errorState = awaitItem() as ListUiState.Error
            assertEquals("Network Error", errorState.message)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `toggleFavorite calls repository and reloads list`() = runTest {
        // GIVEN
        val product = Product(1, "Test", 10.0, "", "", "", false)

        // Stubbing
        coEvery { repository.getProducts(any()) } returns listOf(product)
        // È buona norma stubbare esplicitamente anche i metodi Unit, anche se relaxed=true lo fa per te
        coEvery { repository.toggleFavorite(any()) } returns Unit

        viewModel = ProductsListViewModel(repository)

        // 1. Consumiamo lo stato iniziale (Loading -> Success)
        // Questo è importante per assicurarci che 'init' sia finito completamente
        viewModel.uiState.test {
            assertEquals(ListUiState.Loading, awaitItem())
            assertEquals(ListUiState.Success(listOf(product)), awaitItem())
        }

        // WHEN
        viewModel.toggleFavorite(product)

        // 🔥 FIX FONDAMENTALE:
        // Forza l'esecuzione di tutte le coroutine in attesa (incluso il launch di toggleFavorite)
        advanceUntilIdle()

        // THEN
        // 1. Verifica che il metodo toggleFavorite sia stato chiamato
        coVerify(exactly = 1) { repository.toggleFavorite(product) }

        // 2. Verifica che getProducts sia stato chiamato 2 volte (1 per init, 1 per il reload)
        coVerify(exactly = 2) { repository.getProducts(any()) }
    }
}
