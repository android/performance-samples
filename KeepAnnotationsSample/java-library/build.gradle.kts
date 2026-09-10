import org.gradle.kotlin.dsl.support.uppercaseFirstChar

plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.androidx.annotation.keep)
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

val jarTask = project.tasks.named<org.gradle.jvm.tasks.Jar>("jar")
val outputJar = jarTask.flatMap { it.archiveFile }
val outputFile = jarTask.flatMap { it.archiveFileName }.map { fileName ->
    "keep${fileName.uppercaseFirstChar()}"
}

// Register the JAR transform for extracting keep rules.
val artifactTask = annotationKeep.registerJavaArchiveTransform(
    taskName = "keepRulesJar",
    input = outputJar,
    fileName = outputFile
)

// Register the new artifact.
project.configurations.configureEach {
    if (name == "apiElements" || name == "runtimeElements") {
        outgoing.artifact(artifactTask.flatMap { it.outputJar })
    }
}

dependencies {
    implementation(libs.androidx.annotation.keep)
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}
