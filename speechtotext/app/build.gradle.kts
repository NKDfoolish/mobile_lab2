plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.ex2"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.ex2"
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
}

dependencies {
    // OkHttp for HTTP requests to LibreTranslate
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    // Material Components for UI
    implementation("com.google.android.material:material:1.9.0")
    // Core Android dependencies
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    // Testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}