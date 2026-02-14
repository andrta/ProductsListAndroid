package com.tamboo.data.di

import com.tamboo.data.datasource.ProductLocalDataSource
import com.tamboo.data.datasource.RealmProductDataSource
import com.tamboo.data.repository.ProductRepositoryImpl
import com.tamboo.domain.repository.ProductRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module

val dataModule = module {
    single { Dispatchers.IO }

    single<ProductLocalDataSource> {
        RealmProductDataSource(get())
    }

    single<ProductRepository> {
        ProductRepositoryImpl(
            api = get(),
            localDataSource = get(),
            ioDispatcher = get()
        )
    }
}
