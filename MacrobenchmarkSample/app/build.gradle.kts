/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("androidx.baselineprofile") version "1.2.0-alpha13"
    //     alias(libs.plugins.baselineprofile)
}

android {
    compileSdk = 33
    namespace = "com.example.macrobenchmark.target"

    defaultConfig {
        applicationId ="com.example.macrobenchmark.target"
        versionCode = 1
        versionName = "1.0"
        minSdk = 23 // Minimum supported version for macrobenchmark
        targetSdk = 33
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        compose = true
        viewBinding = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }

    // [START macrobenchmark_setup_app_build_type]
    buildTypes {
        val release = getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }

        create("benchmark") {
            initWith(release)
            signingConfig = signingConfigs.getByName("debug")
            // [START_EXCLUDE silent]
            // Selects release buildType if the benchmark buildType not available in other modules.
            matchingFallbacks.add("release")
            // [END_EXCLUDE]
            proguardFiles("benchmark-rules.pro")
        }
    }
    // [END macrobenchmark_setup_app_build_type]

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-opt-in=kotlin.RequiresOptIn"
        )
    }
}

dependencies {
    val composeBom = platform(libs.compose.bom)
    implementation(composeBom)

    implementation(libs.activity)
    implementation(libs.appcompat)
    implementation(libs.compose.activity)
    implementation(libs.compose.material)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling)
    implementation(libs.constraintlayout)
    implementation(libs.concurrentfutures)
    implementation(libs.core)
    implementation(libs.datastore)
    implementation(libs.google.material)
    implementation(libs.kotlin.coroutines)
    implementation(libs.kotlin.coroutines.guava)
    implementation(libs.kotlin.stdlib)
    implementation(libs.lifecycle)
    implementation(libs.profileinstaller)
    implementation(libs.squareup.curtains)
    implementation(libs.tracing)
    implementation(libs.viewmodel)
    androidTestImplementation(libs.benchmark.junit)

    baselineProfile(project(":baselineProfile"))
}
