plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.tamboo.productslistandroid"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.tamboo.productslistandroid"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.11"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/LICENSE.md"
            excludes += "META-INF/LICENSE-notice.md"
        }
    }
}

dependencies {
    // --- Core Android ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // --- UI (Compose) ---
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose.ui) // Include UI, Graphics, Preview, M3
    implementation(libs.androidx.compose.icons.extended)
    implementation(libs.coil.compose)

    // --- Navigation ---
    implementation(libs.androidx.navigation.compose)

    // --- Realm ---
    implementation(libs.realm.base)
    
    // --- Dependency Injection ---
    implementation(libs.bundles.koin) // Include Koin Android & Compose

    // --- Networking (Se l'App module deve inizializzare client specifici) ---
    implementation(libs.bundles.networking)

    // --- Project Modules ---
    // Core Layers
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(project(":core:domain"))
    implementation(project(":core:data"))
    implementation(project(":core:ui"))

    // Features
    implementation(project(":features:productslist"))
    implementation(project(":features:favorites"))
    implementation(project(":features:profile"))

    // --- Testing ---
    // Unit Tests
    testImplementation(libs.junit)

    // Android/Instrumented Tests
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    // E2E Testing Specifics
    androidTestImplementation(libs.mockwebserver)
    androidTestImplementation(libs.koin.test)

    // --- Debugging ---
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
