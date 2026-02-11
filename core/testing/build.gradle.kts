plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    api(libs.junit)
    api(libs.kotlinx.coroutines.test)
    api(libs.mockk)
    api(libs.turbine)
    api(libs.truth)
}
