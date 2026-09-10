# KeepAnnotations Sample

This examples shows the use of the `androidx.annotation.keep` Gradle plugin.

- This programmatically generates `keep` rules when an `app` / `android library` / `java library` module uses `androidx.annotation:annotation-keep`.

## Testing

### Android apps and Library Modules

```bash

// App Modules

./gradlew <variantName>ExtractKeepRules

// For Android Library Modules

./gradlew <variantName>KeepRulesTransformAar
```

### Java / Kotlin Library Modules

For Java Libraries, you need to do the following. 

* Apply the Gradle Plugin.

```kotlin
plugins {
    alias(libs.plugins.androidx.annotation.keep)
}
```

* Register a new task that can generate the artifact with keep rules.

```kotlin
val outputJar = project.tasks.named<org.gradle.jvm.tasks.Jar>("jar").flatMap { task ->
    task.archiveFile
}

val outputFile = outputJar.map { file -> "keep${file.asFile.name.uppercaseFirstChar()}" }

// Register the JAR transform for extracting keep rules.
val artifactTask = annotationKeep.registerJavaArchiveTransform(
    taskName = "keepRulesJar",
    input = outputJar,
    fileName = outputFile
)
```

* Register the output artifact for publication.

```kotlin
// Register the new artifact.
project.configurations.configureEach {
    if (name == "apiElements" || name == "runtimeElements") {
        outgoing.artifact(artifactTask)
    }
}
```
