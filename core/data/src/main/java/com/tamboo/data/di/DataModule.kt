package com.tamboo.data.di

import com.tamboo.data.datasource.ProductLocalDataSource
import com.tamboo.data.datasource.RealmProductDataSource
import com.tamboo.data.repository.ProductRepositoryImpl
import com.tamboo.domain.repository.ProductRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val dataModule = module {
    singleOf(::RealmProductDataSource) { bind<ProductLocalDataSource>() }

    singleOf(::ProductRepositoryImpl) { bind<ProductRepository>() }
}
