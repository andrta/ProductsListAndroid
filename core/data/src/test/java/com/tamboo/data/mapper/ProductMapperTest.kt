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
        // GIVEN - Un DTO simulato (come se arrivasse dalla rete)
        val dto = ProductDto(
            id = 101,
            title = "Zaino Test",
            price = 45.50,
            description = "Descrizione test",
            category = "Accessori",
            image = "https://example.com/image.png",
            rating = RatingDto(rate = 4.5, count = 100) // Assumendo che il DTO abbia questo campo
        )
        val isFavoriteExpected = true

        // WHEN - Eseguiamo il mapping
        val domainModel = dto.toDomain(isFavorite = isFavoriteExpected)

        // THEN - Verifichiamo che i dati siano stati copiati correttamente
        assertEquals(dto.id, domainModel.id)
        assertEquals(dto.title, domainModel.title)
        assertEquals(dto.price, domainModel.price, 0.0) // 0.0 è la tolleranza per i double
        assertEquals(dto.description, domainModel.description)
        assertEquals(dto.category, domainModel.category)

        // Verifica cruciale: il campo 'image' del DTO diventa 'imageUrl' nel dominio
        assertEquals(dto.image, domainModel.imageUrl)

        // Verifica il flag passato come argomento
        assertTrue("Il flag isFavorite dovrebbe essere true", domainModel.isFavorite)
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
        // GIVEN - Un'entità simulata (come se arrivasse dal DB Realm)
        val entity = ProductEntity().apply {
            id = 202
            title = "Maglietta Realm"
            price = 19.99
            description = "Dal Database"
            category = "Abbigliamento"
            imageUrl = "https://example.com/db_image.jpg"
            isFavorite = true
        }

        // WHEN - Eseguiamo il mapping
        val domainModel = entity.toDomain()

        // THEN
        assertEquals(entity.id, domainModel.id)
        assertEquals(entity.title, domainModel.title)
        assertEquals(entity.price, domainModel.price, 0.0)
        assertEquals(entity.description, domainModel.description)
        assertEquals(entity.category, domainModel.category)

        // Verifica cruciale: Entity ha 'imageUrl', Domain ha 'imageUrl'
        assertEquals(entity.imageUrl, domainModel.imageUrl)

        // Verifica che il flag isFavorite sia stato preservato dal DB
        assertEquals(entity.isFavorite, domainModel.isFavorite)
    }
}
