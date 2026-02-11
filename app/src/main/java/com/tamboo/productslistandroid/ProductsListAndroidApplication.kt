package com.tamboo.productslistandroid

import android.app.Application
import com.tamboo.data.di.dataModule
import com.tamboo.database.di.databaseModule
import com.tamboo.network.di.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ProductsListAndroidApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@ProductsListAndroidApplication)
            modules(networkModule,
                databaseModule,
                dataModule,
            )
        }
    }
}
