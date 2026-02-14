package com.tamboo.profile.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NameDto(
    @param:Json(name = "firstname") val firstname: String,
    @param:Json(name = "lastname") val lastname: String
)
