plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.ex2_3"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.ex2_3"
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

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.squareup.okhttp3:okhttp:4.10.0") // Để gọi API
    implementation("androidx.appcompat:appcompat:1.6.1") // Hỗ trợ giao diện
    implementation("androidx.core:core:1.12.0") // Hỗ trợ core
    implementation("org.pytorch:pytorch_android_lite:1.13.1")
    implementation("org.pytorch:pytorch_android_torchvision_lite:1.13.1")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.apache.commons:commons-text:1.10.0")

}