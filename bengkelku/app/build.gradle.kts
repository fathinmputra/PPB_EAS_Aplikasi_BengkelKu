plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    // Firebase plugin commented for demo mode
    // id("com.google.gms.google-services")
}

android {
    namespace = "com.example.bengkelku"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.bengkelku"
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
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.7.6")

    // ViewModel Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")

    // Firebase dependencies - COMMENTED FOR DEMO MODE
    // implementation(platform("com.google.firebase:firebase-bom:32.7.1"))
    // implementation("com.google.firebase:firebase-auth-ktx")
    // implementation("com.google.firebase:firebase-firestore-ktx")
    // implementation("com.google.firebase:firebase-analytics-ktx")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    // implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    // Image loading
    implementation("io.coil-kt:coil-compose:2.5.0")

    // Extended Material Icons
    implementation("androidx.compose.material:material-icons-extended:1.5.8")

    // Date Picker
    implementation("io.github.vanpra.compose-material-dialogs:datetime:0.9.0")

    // Gson for local storage (NEW)
    implementation("com.google.code.gson:gson:2.10.1")

    // Test dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}