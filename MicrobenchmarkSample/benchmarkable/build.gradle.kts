plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    compileSdk = 34
    namespace = "com.example.benchmark.ui"

    defaultConfig {
        minSdk = 21
        targetSdk = 34
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_18
        targetCompatibility = JavaVersion.VERSION_18
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_18.toString()
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.cardview)
    implementation(libs.constraintlayout)
    implementation(libs.kotlin.stdlib)
    implementation(libs.recyclerview)

    androidTestImplementation(libs.espresso)
    androidTestImplementation(libs.test.ext)
    androidTestImplementation(libs.test.runner)

    testImplementation(libs.junit)
}
