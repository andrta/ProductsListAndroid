package com.tamboo.profile.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AddressDto(
    @param:Json(name = "city") val city: String,
    @param:Json(name = "street") val street: String,
    @param:Json(name = "number") val number: Int,
    @param:Json(name = "zipcode") val zipcode: String
)
