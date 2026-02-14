package com.tamboo.profile.domain.usecase

import com.tamboo.profile.domain.model.User
import com.tamboo.profile.domain.repository.ProfileRepository

class GetUserProfileUseCase(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(): Result<User> {
        return repository.getUser(USER_ID)
    }
}

private const val USER_ID = 8
