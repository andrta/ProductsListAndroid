package com.tamboo.productlistandroid.e2e

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.tamboo.data.di.dataModule
import com.tamboo.database.model.ProductEntity
import com.tamboo.domain.di.domainModule
import com.tamboo.network.service.FakeStoreApi
import com.tamboo.productslist.di.productsListModule
import com.tamboo.productslist.presentation.ProductsListScreen
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@RunWith(AndroidJUnit4::class)
class ProductsListIntegrationTest : KoinTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockWebServer: MockWebServer
    private lateinit var realm: Realm

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val successResponse = """
            [
                {
                    "id": 1,
                    "title": "Back bag Creative Tech",
                    "price": 109.95,
                    "description": "Back bag perfect for Software Developers",
                    "category": "men's clothing",
                    "image": "https://fakestoreapi.com/img/81fPKd-2AYL._AC_SL1500_.jpg",
                    "rating": { "rate": 3.9, "count": 120 }
                }
            ]
        """
        mockWebServer.enqueue(MockResponse().setBody(successResponse).setResponseCode(200))

        val baseUrlString = runBlocking(Dispatchers.IO) {
            mockWebServer.url("/").toString()
        }

        val realmConfig = RealmConfiguration.Builder(schema = setOf(ProductEntity::class))
            .inMemory()
            .name("test-realm")
            .build()
        realm = Realm.open(realmConfig)

        stopKoin()
        startKoin {
            modules(
                module {
                    single { Moshi.Builder().add(KotlinJsonAdapterFactory()).build() }
                    single {
                        Retrofit.Builder()
                            .baseUrl(baseUrlString)
                            .addConverterFactory(MoshiConverterFactory.create(get()))
                            .build()
                            .create(FakeStoreApi::class.java)
                    }
                },
                module {
                    single { realm }
                },
                domainModule,
                dataModule,
                productsListModule
            )
        }
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
        if (::realm.isInitialized && !realm.isClosed()) {
            realm.close()
        }
        stopKoin()
    }

    @Test
    fun app_displays_products_fetched_from_network() {
        // WHEN
        composeTestRule.setContent {
            ProductsListScreen()
        }

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Back bag Creative Tech")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // THEN
        composeTestRule.onNodeWithText("Back bag Creative Tech").assertIsDisplayed()
    }
}
