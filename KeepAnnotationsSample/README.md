# KeepAnnotations Sample

This examples shows the use of the `androidx.annotation.keep` Gradle plugin.

- This programmatically generates `keep` rules when an `app` / `android library` / `java library` module uses `androidx.annotation:annotation-keep`.


## Testing

```kotlin

// For app modules

./gradlew <variantName>ExtractKeepRules

// For android library modules

./gradlew <variantName>KeepRulesTransformAar

```

The generated keep rules end up in `build/generated/variantName/androidx.annotation.keep.rules.pro`.
