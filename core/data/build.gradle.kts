plugins {
    // CAMBIO FONDAMENTALE: Usa i plugin Android, NON kotlin.jvm
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.tamboo.productslistandroid.core.data"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
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
    implementation(project(":core:domain"))   // Questo è Kotlin Puro (OK, Android può vedere Kotlin)
    implementation(project(":core:network"))  // Questo è Kotlin Puro (OK)
    implementation(project(":core:database")) // Questo è Android (OK, ora siamo Android anche noi)

    // Questo impedisce che MockK o altre lib tirino dentro Kotlin 2.1
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.9.23"))

    // Koin & Coroutines
    implementation(libs.koin.android)
    implementation(libs.androidx.core.ktx)

    // Realm (Necessario per vedere gli oggetti Realm restituiti dal DB)
    implementation(libs.realm.base)

    implementation(libs.kotlinx.coroutines.core)

    // Test Dependencies
    testImplementation(libs.junit)
    testImplementation(libs.mockk) // Fondamentale per mockare API e DB
    testImplementation(libs.kotlinx.coroutines.test) // Per testare le sospensioni
    testImplementation(libs.turbine) // Opzionale, ma ottimo per testare i Flow
}

// 🔥 AGGIUNGI QUESTO ALLA FINE DEL FILE
// Forza Gradle a usare versioni compatibili anche se altre lib ne chiedono di più nuove
configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "org.jetbrains.kotlin" && requested.name.startsWith("kotlin-stdlib")) {
            useVersion("1.9.23")
        }
        if (requested.group == "org.jetbrains.kotlinx" && requested.name.startsWith("kotlinx-coroutines")) {
            useVersion("1.7.3")
        }
    }
}
