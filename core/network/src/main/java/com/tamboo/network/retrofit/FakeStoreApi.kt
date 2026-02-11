package com.tamboo.network.retrofit

import com.tamboo.network.model.ProductDto
import retrofit2.http.GET

interface FakeStoreApi {
    @GET("products")
    suspend fun getProducts(): List<ProductDto>
}
