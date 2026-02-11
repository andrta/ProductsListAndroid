plugins {
    alias(libs.plugins.kotlin.jvm) // Usa il plugin JVM puro
}

dependencies {
    // Networking (Puri Java/Kotlin)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.moshi)
    implementation(libs.moshi.kotlin)

    // Koin (Solo Core, niente Android)
    implementation(libs.koin.core)

    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
}