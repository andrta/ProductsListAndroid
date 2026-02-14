package com.tamboo.domain.usecase

import app.cash.turbine.test
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

class ObserveFavoriteIdsUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: ProductRepository = mockk()

    private lateinit var useCase: ObserveFavoriteIdsUseCase

    @Before
    fun setup() {
        useCase = ObserveFavoriteIdsUseCase(repository)
    }


    @Test
    fun `invoke calls repository observeFavoriteIds`() = runTest {
        // GIVEN
        val expectedIds = setOf(10, 20, 30)
        every { repository.observeFavoriteIds() } returns flowOf(expectedIds)

        // WHEN & THEN
        useCase().test {
            val result = awaitItem()
            assertEquals(expectedIds, result)
            awaitComplete()
        }

        verify(exactly = 1) { repository.observeFavoriteIds() }
    }
}
