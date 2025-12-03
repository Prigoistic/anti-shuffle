# gallery_app

Starter Android app (Jetpack Compose + Hilt) with Gradle wrapper and Kotlin DSL.

## Requirements
- macOS (Intel or Apple Silicon)
- JDK 17 (Temurin/OpenJDK 17)
- Android SDK, emulator

Versions pinned:
- Gradle wrapper: 8.5
- Android Gradle Plugin: 8.1.1
- Kotlin: 1.9.22
- compileSdk/targetSdk: 34, minSdk: 26

## Setup JDK 17
Check your Java version:

```
java -version
# expect: openjdk version "17.x"
```

If not 17, install Temurin 17 and set it for Gradle/Android Studio:

```
brew install --cask temurin17
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
echo "export JAVA_HOME=$(/usr/libexec/java_home -v 17)" >> ~/.zshrc
```

In Android Studio: Settings > Build, Execution, Deployment > Build Tools > Gradle > Gradle JDK = JDK 17.

## Build

```
./gradlew --no-daemon clean assembleDebug --stacktrace --info
```

If you see errors referencing Java 25 or Gradle 8.14, ensure you are using JDK 17 and the provided Gradle wrapper (8.5). Set JAVA_HOME as above and re-run.

## Run

```
./gradlew installDebug
```

Launch on an emulator; the app shows: "Smart Gallery Starting Point".

## Project layout
- settings.gradle.kts: plugin versions & repositories
- build.gradle.kts: minimal root
- app/: Android application module
  - build.gradle.kts
  - src/main/AndroidManifest.xml
  - src/main/java/com/gallery_app/App.kt (@HiltAndroidApp)
  - src/main/java/com/gallery_app/MainActivity.kt (Compose UI)
  - src/main/res/values/themes.xml

## Branch
All changes are in branch `init/gradle-setup`.
