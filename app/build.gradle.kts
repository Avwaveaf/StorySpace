plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("androidx.navigation.safeargs")
    id("kotlin-parcelize")
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
}

android {
    namespace = "com.avwaveaf.storyspace"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.avwaveaf.storyspace"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    //PAGING RUNTIME
    implementation(libs.androidx.paging.runtime.ktx)

    // GMS
    implementation (libs.play.services.base)

    // Views/Fragments integration
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)

    // WorkManager
    implementation(libs.androidx.work.runtime.ktx)

    // exinterface
    implementation(libs.androidx.exifinterface)

    // camera x deps
    implementation(libs.androidx.camera.camera2)
    implementation(libs.camera.lifecycle)
    implementation(libs.camera.view)

    // Glide deps
    implementation (libs.glide)

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    kapt(libs.hilt.android.compiler)
    kapt (libs.androidx.hilt.compiler)
    implementation(libs.androidx.hilt.common)
    implementation(libs.androidx.hilt.work)

    // Retrofit and Gson
    implementation(libs.retrofit2.retrofit)
    implementation(libs.converter.gson)

    // logging interceptor
    implementation(libs.logging.interceptor)

    // ViewModel and LiveData
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // Coroutine support for Retrofit
    implementation(libs.jetbrains.kotlinx.coroutines.core)
    //noinspection GradleDependency
    implementation(libs.kotlinx.coroutines.android)

    // Data Store Deps
    implementation(libs.androidx.datastore.preferences)

    // Preference Deps
    implementation(libs.androidx.preference)


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}