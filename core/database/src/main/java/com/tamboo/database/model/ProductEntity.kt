package com.tamboo.database.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class ProductEntity : RealmObject {
    @PrimaryKey
    var id: Int = 0
    var title: String = ""
    var price: Double = 0.0
    var description: String = ""
    var category: String = ""
    var imageUrl: String = ""
    var isFavorite: Boolean = false
    var lastUpdated: Long = System.currentTimeMillis()
}
