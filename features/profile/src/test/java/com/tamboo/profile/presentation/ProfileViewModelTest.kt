package com.tamboo.profile.presentation

import app.cash.turbine.test
import com.tamboo.domain.model.Product
import com.tamboo.domain.usecase.GetFavoriteProductsUseCase
import com.tamboo.profile.domain.model.User
import com.tamboo.profile.domain.usecase.GetUserProfileUseCase
import com.tamboo.profile.presentation.uistate.ProfileUiState
import com.tamboo.testing.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class ProfileViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getUserUseCase: GetUserProfileUseCase = mockk()
    private val getFavoritesUseCase: GetFavoriteProductsUseCase = mockk()
    private lateinit var viewModel: ProfileViewModel

    @Test
    fun `loadData sets Success state with user 8 and favorites count`() = runTest {
        // GIVEN
        val mockUser = getMockUser()
        val mockFavorites = listOf(mockk<Product>(), mockk<Product>())

        coEvery { getUserUseCase() } returns Result.success(mockUser)
        every { getFavoritesUseCase() } returns flowOf(mockFavorites)

        // WHEN
        viewModel = ProfileViewModel(getUserUseCase, getFavoritesUseCase)

        // THEN
        viewModel.uiState.test {
            val state = awaitItem() as ProfileUiState.Success
            assert(state.user.id == 8)
            assert(state.favoritesCount == 2)
        }
    }
}

private fun getMockUser() = User(
    8,
    "test@test.com",
    "username",
    "First",
    "Last",
    "123",
    mockk()
)
