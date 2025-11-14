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
    alias(libs.plugins.application)
    alias(libs.plugins.compose)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.baselineprofile)
}

android {
    compileSdk = 36
    namespace = "com.example.macrobenchmark.target"

    defaultConfig {
        applicationId = "com.example.macrobenchmark.target"
        versionCode = 1
        versionName = "1.0"
        minSdk = 24
        targetSdk = 36
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        compose = true
        viewBinding = true
    }

    // [START macrobenchmark_setup_app_build_type]
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "keep-rules.pro"
            )
            // In real app, this would use its own release keystore
            signingConfig = signingConfigs.getByName("debug")
            baselineProfile.automaticGenerationDuringBuild = true
        }
    }
    // [END macrobenchmark_setup_app_build_type]

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
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
    implementation(libs.compose.runtime.tracing)
    implementation(libs.constraintlayout)
    implementation(libs.concurrentfutures)
    implementation(libs.core)
    implementation(libs.datastore)
    implementation(libs.google.material)
    implementation(libs.kotlin.coroutines)
    implementation(libs.kotlin.coroutines.guava)
    implementation(libs.lifecycle)
    implementation(libs.profileinstaller)
    implementation(libs.squareup.curtains)
    implementation(libs.tracing)
    implementation(libs.viewmodel)
    androidTestImplementation(libs.benchmark.junit)

    baselineProfile(project(":macrobenchmark"))
}
