package com.tamboo.data.mapper

import com.tamboo.database.model.ProductEntity
import com.tamboo.network.model.ProductDto
import com.tamboo.network.model.RatingDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductMapperTest {

    @Test
    fun `ProductDto_toDomain maps fields correctly and sets favorite flag`() {
        // GIVEN
        val dto = ProductDto(
            id = 101,
            title = "Title",
            price = 45.50,
            description = "Desciption",
            category = "A",
            image = "https://example.com/image.png",
            rating = RatingDto(rate = 4.5, count = 100)
        )
        val isFavoriteExpected = true

        // WHEN
        val domainModel = dto.toDomain(isFavorite = isFavoriteExpected)

        // THEN
        assertEquals(dto.id, domainModel.id)
        assertEquals(dto.title, domainModel.title)
        assertEquals(dto.price, domainModel.price, 0.0)
        assertEquals(dto.description, domainModel.description)
        assertEquals(dto.category, domainModel.category)
        assertEquals(dto.image, domainModel.imageUrl)
        assertTrue(domainModel.isFavorite)
    }

    @Test
    fun `ProductDto_toDomain sets isFavorite to false when requested`() {
        // GIVEN
        val dto = ProductDto(
            id = 1, title = "A", price = 1.0, description = "B", category = "C", image = "D",
            rating = RatingDto(1.0, 1)
        )

        // WHEN
        val domainModel = dto.toDomain(isFavorite = false)

        // THEN
        assertFalse(domainModel.isFavorite)
    }

    @Test
    fun `ProductEntity_toDomain maps all fields correctly`() {
        // GIVEN
        val entity = ProductEntity().apply {
            id = 202
            title = "Title"
            price = 19.99
            description = "Description"
            category = "A"
            imageUrl = "https://example.com/db_image.jpg"
            isFavorite = true
        }

        // WHEN
        val domainModel = entity.toDomain()

        // THEN
        assertEquals(entity.id, domainModel.id)
        assertEquals(entity.title, domainModel.title)
        assertEquals(entity.price, domainModel.price, 0.0)
        assertEquals(entity.description, domainModel.description)
        assertEquals(entity.category, domainModel.category)
        assertEquals(entity.imageUrl, domainModel.imageUrl)
        assertEquals(entity.isFavorite, domainModel.isFavorite)
    }
}
