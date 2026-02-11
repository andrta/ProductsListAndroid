package com.tamboo.domain.usecase

import com.tamboo.domain.model.Product
import com.tamboo.domain.repository.ProductRepository
import com.tamboo.testing.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class GetProductsUseCaseTest {

    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    private val repository: ProductRepository = mockk()
    private val useCase = GetProductsUseCase(repository)

    @Test
    fun `invoke should call repository getProducts`() = runTest {
        val expected = listOf(Product(1, "Test", 10.0, "", "", "", false))
        coEvery { repository.getProducts(any()) } returns expected

        val result = useCase(forceUpdate = true)

        assertEquals(expected, result)
        coVerify(exactly = 1) { repository.getProducts(true) }
    }
}
