
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("plugin.serialization") version "1.9.10"
}

android {
    packaging {
        resources {
            excludes += "META-INF/versions/9/OSGI-INF/MANIFEST.MF"
        }
    }
    namespace = "com.example.geninterviewsampleapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.geninterviewsampleapp"
        minSdk = 24
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.material3)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.coil.compose)
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.compose.testing)
    testImplementation(libs.junit)

    // Align versions of androidx.test.ext:junit and androidx.test.espresso:espresso-core
    androidTestImplementation ("androidx.test.ext:junit:1.1.5")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.5.0")

    // Ensure Compose testing dependencies are compatible
    androidTestImplementation ("androidx.compose.ui:ui-test-junit4:1.5.0")
    debugImplementation ("androidx.compose.ui:ui-tooling:1.5.0")
    debugImplementation ("androidx.compose.ui:ui-test-manifest:1.5.0")
    // Robolectric for unit tests
    testImplementation ("org.robolectric:robolectric:4.10.3")

    testImplementation ("androidx.test:core:1.5.0")
}
