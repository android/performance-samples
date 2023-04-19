import com.android.build.api.dsl.ManagedVirtualDevice

plugins {
    alias(libs.plugins.test)
    alias(libs.plugins.kotlin)
}

// [START macrobenchmark_setup_android]
android {
    // [START_EXCLUDE]
    compileSdk = 33
    namespace = "com.example.macrobenchmark"

    defaultConfig {
        minSdk = 23 // Minimum supported version for Macrobenchmark
        targetSdk = 33
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    targetProjectPath = ":app"

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }

    testOptions.managedDevices.devices {
        create<ManagedVirtualDevice>("pixel6Api31") {
            device = "Pixel 6"
            apiLevel = 31
            systemImageSource = "aosp"
        }
    }
    // [END_EXCLUDE]
    // Note that your module name may have different name
    targetProjectPath = ":app"
    // Enable the benchmark to run separately from the app process
    experimentalProperties["android.experimental.self-instrumenting"] = true

    buildTypes {
        // declare a build type to match the target app"s build type
        create("benchmark") {
            isDebuggable = true
            signingConfig = signingConfigs.getByName("debug")
            // [START_EXCLUDE silent]
            // Selects release buildType if the benchmark buildType not available in other modules.
            matchingFallbacks.add("release")
            // [END_EXCLUDE]
        }
    }
}
// [END macrobenchmark_setup_android]

//// [START macrobenchmark_setup_variant]
androidComponents {
    beforeVariants(selector().all()) {
        // enable only the benchmark buildType, since we only want to measure close to release performance
        it.enable = it.buildType == "benchmark"
    }
}
//// [END macrobenchmark_setup_variant]

dependencies {
    implementation(project(":baseBenchmarks"))
    implementation(libs.benchmark.junit)
    implementation(libs.androidx.junit)
    implementation(libs.espresso)
    implementation(libs.ui.automator)
}
