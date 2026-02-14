plugins {
    alias(libs.plugins.kotlin.jvm)
}
dependencies {
    implementation(libs.koin.core)
    implementation(libs.kotlinx.coroutines.core)

    testImplementation(project(":core:testing"))
}
