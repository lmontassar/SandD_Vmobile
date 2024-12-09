plugins {
    alias(libs.plugins.android.application)
    kotlin("android") version "1.9.20"
    kotlin("kapt") version "1.9.20"
}

android {
    namespace = "com.example.sandd_vmobile"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.sandd_vmobile"
        minSdk = 24
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // OkHttp for efficient HTTP requests
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")

    // Glide for image loading and caching
    implementation("com.github.bumptech.glide:glide:4.12.0")
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    kapt("com.github.bumptech.glide:compiler:4.12.0")

    implementation("com.google.code.gson:gson:2.10.1")

    // CircleImageView for rounded images
    implementation("de.hdodenhof:circleimageview:3.1.0")

    implementation(libs.material)
    implementation(libs.appcompat)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}