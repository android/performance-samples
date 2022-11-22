plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    compileSdk = 33
    namespace = "com.example.benchmark.ui"

    defaultConfig {
        minSdk = 21
        targetSdk = 33
    }

    kotlinOptions {
        jvmTarget = "11"
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
