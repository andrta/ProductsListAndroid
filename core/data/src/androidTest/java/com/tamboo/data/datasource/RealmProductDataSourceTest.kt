package com.tamboo.data.datasource

import com.tamboo.database.model.ProductEntity
import com.tamboo.network.model.ProductDto
import com.tamboo.network.model.RatingDto
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RealmProductDataSourceTest {

    private lateinit var realm: Realm
    private lateinit var dataSource: RealmProductDataSource

    @Before
    fun setup() {
        val config = RealmConfiguration.Builder(schema = setOf(ProductEntity::class))
            .inMemory()
            .name("test-realm-instrumented-${System.currentTimeMillis()}")
            .build()
        realm = Realm.open(config)
        dataSource = RealmProductDataSource(realm)
    }

    @After
    fun tearDown() {
        if (!realm.isClosed()) {
            realm.close()
        }
    }

    @Test
    fun cacheResponse_saves_data_and_preserves_isFavorite_status() = runTest {
        // GIVEN
        realm.write {
            copyToRealm(ProductEntity().apply {
                id = 99
                title = "Old Title"
                price = 10.0
                isFavorite = true
                lastUpdated = 0L
            })
        }

        val networkData = listOf(
            ProductDto(
                id = 99,
                title = "New Title",
                price = 20.0,
                description = "New Description",
                category = "Cat",
                image = "img_url",
                rating = RatingDto(4.5, 100)
            )
        )

        // WHEN
        dataSource.cacheResponse(networkData)

        // THEN
        val updatedProduct = realm.query<ProductEntity>("id == $0", 99).first().find()
        assertNotNull(updatedProduct)

        assertEquals("New Title", updatedProduct?.title)
        assertEquals(20.0, updatedProduct?.price ?: 0.0, 0.0)

        assertTrue(updatedProduct?.isFavorite == true)

        assertTrue(updatedProduct!!.lastUpdated > 0L)
    }

    @Test
    fun toggleFavorite_toggles_flag_on_existing_product() = runTest {
        // GIVEN
        realm.write {
            copyToRealm(ProductEntity().apply {
                id = 10
                title = "Test"
                isFavorite = true
            })
        }

        // WHEN
        dataSource.toggleFavorite(10, "Test", 10.0, "", "", "")

        // THEN
        val product = realm.query<ProductEntity>("id == $0", 10).first().find()
        assertNotNull(product)
        assertFalse(product?.isFavorite == true)

        // WHEN
        dataSource.toggleFavorite(10, "Test", 10.0, "", "", "")

        // THEN
        val productAgain = realm.query<ProductEntity>("id == $0", 10).first().find()
        assertTrue(productAgain?.isFavorite == true)
    }

    @Test
    fun toggleFavorite_creates_new_product_if_not_exists() = runTest {
        // GIVEN
        val newId = 55

        // WHEN
        dataSource.toggleFavorite(newId, "New Product", 99.9, "img", "desc", "cat")

        // THEN
        val created = realm.query<ProductEntity>("id == $0", newId).first().find()
        assertNotNull(created)
        assertTrue(created?.isFavorite == true)
        assertEquals("New Product", created?.title)
    }

    @Test
    fun getAllProducts_returns_everything() = runTest {
        realm.write {
            copyToRealm(ProductEntity().apply { id = 1 })
            copyToRealm(ProductEntity().apply { id = 2 })
            copyToRealm(ProductEntity().apply { id = 3 })
        }

        val list = dataSource.getAllProducts()
        assertEquals(3, list.size)
    }

    @Test
    fun getFavoriteIds_returns_only_favorites() = runTest {
        realm.write {
            copyToRealm(ProductEntity().apply { id = 1; isFavorite = true })
            copyToRealm(ProductEntity().apply { id = 2; isFavorite = false })
            copyToRealm(ProductEntity().apply { id = 3; isFavorite = true })
        }

        val favIds = dataSource.getFavoriteIds()

        assertEquals(2, favIds.size)
        assertTrue(favIds.contains(1))
        assertTrue(favIds.contains(3))
        assertFalse(favIds.contains(2))
    }

    @Test
    fun isCacheValid_logic_works_correctly() = runTest {
        val now = System.currentTimeMillis()
        val expirationTime = 1000 * 60 * 5L

        // CASE 1: Valid Cache
        realm.write {
            delete(query<ProductEntity>())
            copyToRealm(ProductEntity().apply { id = 1; lastUpdated = now - (1000 * 60) })
        }
        assertTrue(dataSource.isCacheValid(expirationTime))

        // CASE 2: Expired Cache
        realm.write {
            delete(query<ProductEntity>())
            copyToRealm(ProductEntity().apply { id = 2; lastUpdated = now - (1000 * 60 * 10) })
        }
        assertFalse(dataSource.isCacheValid(expirationTime))

        // CASE 3: Empty Cache
        realm.write { delete(query<ProductEntity>()) }
        assertFalse(dataSource.isCacheValid(expirationTime))
    }

    @Test
    fun getFavoriteProductsStream_emits_updates() = runTest {
        val firstEmission = dataSource.getFavoriteProductsStream().first()
        assertTrue(firstEmission.isEmpty())

        dataSource.toggleFavorite(100, "Stream Test", 10.0, "", "", "")

        val secondEmission = dataSource.getFavoriteProductsStream().first()
        assertEquals(1, secondEmission.size)
        assertEquals(100, secondEmission[0].id)
    }
}
