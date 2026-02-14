package com.tamboo.productslistandroid

import android.app.Application
import com.tamboo.data.di.dataModule
import com.tamboo.database.di.databaseModule
import com.tamboo.domain.di.domainModule
import com.tamboo.favorites.di.favoritesModule
import com.tamboo.network.di.networkModule
import com.tamboo.productslist.di.productsListModule
import com.tamboo.profile.di.userProfileModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ProductsListAndroidApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@ProductsListAndroidApplication)
            modules(
                networkModule,
                databaseModule,
                dataModule,
                domainModule,
                productsListModule,
                favoritesModule,
                userProfileModule,
            )
        }
    }
}
