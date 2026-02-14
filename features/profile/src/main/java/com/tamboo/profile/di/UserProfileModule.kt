package com.tamboo.profile.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.tamboo.profile.data.repository.ProfileRepositoryImpl
import com.tamboo.profile.data.service.ProfileApiService
import com.tamboo.profile.domain.repository.ProfileRepository
import com.tamboo.profile.domain.usecase.GetUserProfileUseCase
import com.tamboo.profile.presentation.ProfileViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

val userProfileModule = module {
    single {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    single<ProfileApiService> {
        get<Retrofit>().newBuilder()
            .addConverterFactory(MoshiConverterFactory.create(get()))
            .build()
            .create(ProfileApiService::class.java)
    }

    single<ProfileRepository> { ProfileRepositoryImpl(get()) }
    factory { GetUserProfileUseCase(get()) }

    viewModel { ProfileViewModel(get(), get()) }
}
