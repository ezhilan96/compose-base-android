import org.gradle.configurationcache.extensions.capitalized
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

// Apply plugins required for the project
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.googleDaggerHiltAndroid)
    alias(libs.plugins.googleDevtoolsKsp)
    alias(libs.plugins.googleGmsGoogleServices)
    alias(libs.plugins.googleFirebaseCrashlytics)
    alias(libs.plugins.googleProtobuf)
    alias(libs.plugins.jetbrainsSerialization)
}

android {
    // Define basic project information.
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

    // Define different build types with specific configurations (e.g., release, debug, UAT)
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            resValue(type = "string", name = "base_url", value = "compose.base")
        }

        debug {
            isMinifyEnabled = false
            resValue(type = "string", name = "base_url", value = "uat.compose.base")
            versionNameSuffix = ".debug.uat"
        }

        register("debugR8") {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            resValue(type = "string", name = "base_url", value = "uat.compose.base")
            versionNameSuffix = ".debug.uat.r8"
            signingConfig = signingConfigs.getByName("debug")
        }

        register("debugProd") {
            isDebuggable = true
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            resValue(type = "string", name = "base_url", value = "compose.base")
            versionNameSuffix = ".debug.prod"
            signingConfig = signingConfigs.getByName("debug")
        }

        register("uat") {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            resValue(type = "string", name = "base_url", value = "uat.compose.base")
            versionNameSuffix = ".UAT"
        }
    }

    // Set source and target compatibility for Java and Kotlin
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        //enable Jetpack Compose
        compose = true
        //used to access BuildConfig object inside project
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.11"
    }

    /** Resource Packaging Configuration
    This section configures how resources (like images, layouts, and license files)
    are included in the final APK (application package). By default, Gradle packages
    all resources from your project and its dependencies. However, this can lead to
    conflicts during unit testing, especially with license files.*/
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            merges += "META-INF/LICENSE.md"
            merges += "META-INF/LICENSE-notice.md"
        }
    }
}

// Define third-party dependencies for the project
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
    implementation(libs.androidx.datastore)
    implementation(libs.google.protobuf)
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
    implementation(libs.jetbrains.serialization)
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

// Configure Protobuf Compilation
protobuf {
    // Configures the Protobuf compilation and the protoc executable
    protoc {
        // Downloads the specified version of the Protobuf compiler from the repository
        artifact = "com.google.protobuf:protoc:3.18.0"
    }

    // Generates the java Protobuf-lite code for the Protobufs in this project
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                // Configures the task output type
                create("java") {
                    // Java Lite has smaller code size and is recommended for Android
                    option("lite")
                }
            }
        }
    }
}

/** Configure KSP for DataStore Integration (Fixes 'error.NonExistentClass' Issue)
By setting the source for KSP, we ensure it has access to the generated code representing the
data type for your DataStore. This resolves the "error.NonExistentClass" issue and allows
KSP to process your code correctly. */
androidComponents {
    // Iterate through all build variants
    onVariants(selector().all()) { variant ->
        // This callback ensures KSP runs after all project configurations are finalized
        afterEvaluate {
            // Capitalize the variant name
            val capName = variant.name.capitalized()
            // Retrieve the KSP compilation task for the current variant
            tasks.getByName<KotlinCompile>("ksp${capName}Kotlin") {
                // Set the source for the KSP task to the output of the corresponding proto generation task
                setSource(tasks.getByName("generate${capName}Proto").outputs)
            }
        }
    }
}