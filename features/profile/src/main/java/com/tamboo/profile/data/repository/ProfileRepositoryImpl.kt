package com.tamboo.profile.data.repository

import com.tamboo.profile.data.mapper.toDomain
import com.tamboo.profile.data.service.ProfileApiService
import com.tamboo.profile.domain.model.User
import com.tamboo.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProfileRepositoryImpl(
    private val apiService: ProfileApiService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ProfileRepository {

    override suspend fun getUser(id: Int): Result<User> = withContext(ioDispatcher) {
        try {
            val response = apiService.getUser(id)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
