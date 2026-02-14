package com.tamboo.profile.domain.repository

import com.tamboo.profile.domain.model.User

interface ProfileRepository {
    suspend fun getUser(id: Int): Result<User>
}
