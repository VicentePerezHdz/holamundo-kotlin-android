plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
}

android {
    namespace = "com.holamundo.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.holamundo.app"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
    }

    // Variantes como en AC Móvil: debug (.dev), quality (QA, .qa) y release.
    buildTypes {
        getByName("debug") { applicationIdSuffix = ".dev" }
        create("quality") {
            initWith(getByName("debug"))
            applicationIdSuffix = ".qa"
        }
        getByName("release") { isMinifyEnabled = false }
    }

    buildFeatures {
        viewBinding = true   // código nuevo con ViewBinding (en AC Móvil conviven con ButterKnife)
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":desing"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.activity.ktx)
    kapt(libs.dagger.compiler)
}
