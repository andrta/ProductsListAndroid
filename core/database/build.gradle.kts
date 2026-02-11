plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.realm)
}

android {
    namespace = "com.tamboo.core.database"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
        // Realm richiede un setup specifico per il test runner a volte, ma per ora standard va bene
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(libs.realm.base)
    implementation(libs.koin.android)
    implementation(libs.androidx.core.ktx)
}
