plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.tamboo.data"
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

    testOptions {
        unitTests {
            isReturnDefaultValues = true
            isIncludeAndroidResources = true
        }
    }
}

// 🔥 FIX NUCLEARE PER ROBOLECTRIC + REALM
// Usiamo la resolutionStrategy per SOSTITUIRE fisicamente l'artefatto Android con quello JVM.
// Questo risolve il conflitto che causava la sparizione di "SoLoader".
configurations.all {
    if (name.contains("UnitTest")) {
        resolutionStrategy.eachDependency {
            if (requested.group == "io.realm.kotlin" && requested.name == "library-base") {
                // Forza l'uso di library-base-jvm usando la versione definita nel TOML
                useTarget("io.realm.kotlin:library-base-jvm:${libs.versions.realm.get()}")
                because("I test Robolectric girano su JVM e richiedono i driver nativi per Mac/PC")
            }
        }
    }
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))

    implementation(libs.koin.android)
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.core)

    // Realm (Versione Android standard per l'app)
    implementation(libs.realm.base)

    // Test Dependencies
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.robolectric)
    testImplementation(libs.turbine)

    // Realm JVM: La aggiungiamo, ma la "magia" vera la fa il blocco resolutionStrategy sopra
    testImplementation(libs.realm.base.jvm)
}