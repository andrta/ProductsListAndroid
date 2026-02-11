package com.tamboo.data.mapper

import com.tamboo.database.model.ProductEntity
import com.tamboo.domain.model.Product
import com.tamboo.network.model.ProductDto

fun ProductDto.toDomain(isFavorite: Boolean): Product {
    return Product(
        id = this.id,
        title = this.title,
        price = this.price,
        description = this.description,
        category = this.category,
        imageUrl = this.image,
        isFavorite = isFavorite
    )
}

fun ProductEntity.toDomain(): Product {
    return Product(
        id = this.id,
        title = this.title,
        price = this.price,
        description = this.description,
        category = this.category,
        imageUrl = this.imageUrl,
        isFavorite = this.isFavorite
    )
}
