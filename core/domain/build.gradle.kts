plugins {
    alias(libs.plugins.kotlin.jvm)
}
dependencies {
    // Koin (Solo Core, niente Android)
    implementation(libs.koin.core)

    implementation(libs.kotlinx.coroutines.core)

    // Test
    testImplementation(project(":core:testing"))
}
