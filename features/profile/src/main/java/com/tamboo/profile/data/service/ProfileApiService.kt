package com.tamboo.profile.data.service

import com.tamboo.profile.data.model.UserDto
import retrofit2.http.GET
import retrofit2.http.Path

interface ProfileApiService {
    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: Int): UserDto
}
