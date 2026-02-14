package com.tamboo.domain.di

import com.tamboo.domain.usecase.GetFavoriteProductsUseCase
import com.tamboo.domain.usecase.GetProductsUseCase
import com.tamboo.domain.usecase.ObserveFavoriteIdsUseCase
import com.tamboo.domain.usecase.ToggleFavoriteUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val domainModule = module {
    factoryOf(::GetProductsUseCase)
    factoryOf(::GetFavoriteProductsUseCase)
    factoryOf(::ToggleFavoriteUseCase)
    factoryOf(::ObserveFavoriteIdsUseCase)
}
