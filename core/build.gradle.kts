plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt) // procesador de anotaciones para Dagger 2
}

android {
    namespace = "com.holamundo.core"
    compileSdk = 35
    defaultConfig { minSdk = 24 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}

dependencies {
    // api: quien dependa de :core también ve Dagger y coroutines.
    api(libs.dagger)
    api(libs.coroutines.android)
    kapt(libs.dagger.compiler)

    testImplementation(libs.junit)
}
