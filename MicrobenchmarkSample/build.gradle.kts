// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    alias(libs.plugins.application) apply false
    alias(libs.plugins.benchmark) apply false
    alias(libs.plugins.library) apply false
    alias(libs.plugins.test) apply false
    alias(libs.plugins.kotlin) apply false
}