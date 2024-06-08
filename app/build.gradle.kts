plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services") version "4.3.14"
}

android {
    namespace = "com.daniel_linge.viewreferee"
    compileSdk = 34 // Aktualisiere auf die neueste Version

    defaultConfig {
        applicationId = "com.daniel_linge.viewreferee"
        minSdk = 21
        targetSdk = 34 // Aktualisiere auf die neueste Version
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.10.0") // Neueste stabile Version
    implementation("androidx.appcompat:appcompat:1.6.1") // Neueste stabile Version
    implementation("com.google.android.material:material:1.9.0") // Neueste stabile Version
    implementation("androidx.compose.ui:ui:1.5.0") // Neueste stabile Version
    implementation("androidx.compose.material3:material3:1.1.0") // Neueste stabile Version
    implementation("androidx.compose.ui:ui-tooling-preview:1.5.0") // Neueste stabile Version
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1") // Neueste stabile Version
    implementation("androidx.activity:activity-compose:1.7.2") // Neueste stabile Version
    implementation("com.google.firebase:firebase-firestore:24.4.1") // Neueste stabile Version
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")  // Für LiveData
    implementation("androidx.navigation:navigation-compose:2.6.0")
    implementation(libs.androidx.runtime.livedata) // Füge die Navigation Compose-Bibliothek hinzu

    // Weitere Abhängigkeiten hier hinzufügen

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.5.0")
    debugImplementation("androidx.compose.ui:ui-tooling:1.5.0")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.5.0")
}

// Google Services Plugin hinzufügen
apply(plugin = "com.google.gms.google-services")
