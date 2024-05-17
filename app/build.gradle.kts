plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.googleDaggerHiltAndroid)
    alias(libs.plugins.googleDevtoolsKsp)
    alias(libs.plugins.googleGmsGoogleServices)
    alias(libs.plugins.googleFirebaseCrashlytics)
}

android {
    namespace = "com.compose.base"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.compose.base"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            resValue(type = "string", name = "base_url", value = "https://api.compose.in/")
        }

        debug {
            isMinifyEnabled = false
            resValue(type = "string", name = "base_url", value = "https://api.uat.compose.in/")
            versionNameSuffix = ".debug"
        }

        register("uat") {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            resValue(type = "string", name = "base_url", value = "https://api.uat.compose.in/")
            versionNameSuffix = ".uat"
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
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.11"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            merges += "META-INF/LICENSE.md"
            merges += "META-INF/LICENSE-notice.md"
        }
    }
    configurations {
        all {
            exclude(group = "org.json", module = "json")
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.jetbrains.kotlinx.coroutines.android)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material)
    implementation(libs.androidx.material.icons.extented)
    implementation(libs.androidx.material3)
    implementation(platform(libs.google.firebase.bom))
    implementation(libs.google.firebase.messaging)
    implementation(libs.google.firebase.crashlytics)
    implementation(libs.google.firebase.analytics)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.constraintlayout.compose)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.google.android.material)
    implementation(libs.google.android.play.app.update)
    implementation(libs.google.android.play.app.update.ktx)
    implementation(libs.google.android.gms.play.services.location)
    implementation(libs.google.android.gms.play.services.auth)
    implementation(libs.google.android.gms.play.services.auth.api.phone)
    implementation(libs.google.accompanist.permissions)
    implementation(libs.google.accompanist.systemui)
    implementation(libs.google.dagger.hilt.android)
    ksp(libs.google.dagger.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.squareup.retrofit2.retrofit)
    implementation(libs.squareup.retrofit2.converter.gson)
    implementation(libs.squareup.okhttp3.okhttp)
    implementation(libs.squareup.okhttp3.logging.interceptor)
    implementation(libs.socket.client)
    implementation(libs.coil.kt.coil.compose)
    implementation(libs.ak1.drawbox)
    testImplementation(libs.junit)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.androidx.arch.core.testing)
    testImplementation(libs.jetbrains.kotlinx.coroutines.test)
    testImplementation(libs.google.truth)
    testImplementation(libs.squareup.okhttp3.mockwebserver)
    testImplementation(libs.mockk)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.arch.core.testing)
    androidTestImplementation(libs.jetbrains.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.test.core.ktx)
    androidTestImplementation(libs.google.truth)
    kspAndroidTest(libs.google.dagger.hilt.android.testing)
    androidTestImplementation(libs.squareup.okhttp3.mockwebserver)
    androidTestImplementation(libs.mockk)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}