package com.tamboo.profile.data.repository

import com.tamboo.profile.data.model.UserDto
import com.tamboo.profile.data.service.ProfileApiService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ProfileRepositoryImplTest {
    private val apiService: ProfileApiService = mockk()
    private lateinit var repository: ProfileRepositoryImpl

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        repository = ProfileRepositoryImpl(apiService, testDispatcher)
    }

    @Test
    fun `getUser returns success when API call is successful`() = runTest {
        val mockDto = mockk<UserDto>(relaxed = true)
        coEvery { apiService.getUser(8) } returns mockDto

        val result = repository.getUser(8)

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { apiService.getUser(8) }
    }
}
