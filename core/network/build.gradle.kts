plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.gmazzo.buildconfig)
}

buildConfig {
    packageName("com.tamboo.network")
    buildConfigField("String", "BASE_URL", "\"https://fakestoreapi.com/\"")
}

dependencies {
    implementation(libs.bundles.networking)
    implementation(libs.koin.core)
    implementation(libs.kotlinx.coroutines.core)
}
