package com.tamboo.database.di

import com.tamboo.database.model.ProductEntity
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import org.koin.dsl.module

val databaseModule = module {
    single<Realm> {
        val config = RealmConfiguration.Builder(
            schema = setOf(ProductEntity::class)
        )
            .schemaVersion(1)
            .deleteRealmIfMigrationNeeded()
            .build()

        Realm.open(config)
    }
}
