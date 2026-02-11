package com.tamboo.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProductDto(
    @param:Json(name = "id") val id: Int,
    @param:Json(name = "title") val title: String,
    @param:Json(name = "price") val price: Double,
    @param:Json(name = "description") val description: String,
    @param:Json(name = "category") val category: String,
    @param:Json(name = "image") val image: String,
    @param:Json(name = "rating") val rating: RatingDto?
)

@JsonClass(generateAdapter = true)
data class RatingDto(
    @param:Json(name = "rate") val rate: Double,
    @param:Json(name = "count") val count: Int
)
