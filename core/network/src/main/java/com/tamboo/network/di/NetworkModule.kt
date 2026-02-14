package com.tamboo.network.di

import com.tamboo.network.service.FakeStoreApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

val networkModule = module {

    single {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl("https://fakestoreapi.com/")
            .addConverterFactory(MoshiConverterFactory.create(get()))
            .build()
    }

    single<FakeStoreApi> {
        get<Retrofit>().create(FakeStoreApi::class.java)
    }
}
