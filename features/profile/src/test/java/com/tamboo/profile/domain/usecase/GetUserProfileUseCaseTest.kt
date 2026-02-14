package com.tamboo.profile.domain.usecase

import com.tamboo.profile.domain.repository.ProfileRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class GetUserProfileUseCaseTest {
    private val repository: ProfileRepository = mockk()
    private val useCase = GetUserProfileUseCase(repository)

    @Test
    fun `invoke calls repository with id 8`() = runTest {
        coEvery { repository.getUser(8) } returns Result.success(mockk())

        useCase()

        coVerify(exactly = 1) { repository.getUser(8) }
    }
}
