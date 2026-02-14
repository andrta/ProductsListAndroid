package com.tamboo.profile.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserDto(
    @param:Json(name = "id") val id: Int,
    @param:Json(name = "email") val email: String,
    @param:Json(name = "username") val username: String,
    @param:Json(name = "name") val name: NameDto,
    @param:Json(name = "address") val address: AddressDto,
    @param:Json(name = "phone") val phone: String
)
