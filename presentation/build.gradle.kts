plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id(BuildPlugins.HILT)
}

android {
    compileSdk = ConfigData.compileSdkVersion
    namespace = "app.expense.presentation"

    defaultConfig {
        minSdk = ConfigData.minSdkVersion

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    // Core
    implementation(Deps.Core.DEPENDENCY)

    // Compose
    implementation(Deps.Compose.RUNTIME)

    // Coroutines
    implementation(Deps.Coroutines.DEP)

    // Test
    testImplementation(Deps.JUnit.TEST)

    // Hilt
    implementation(Deps.Hilt.HILT)
    kapt(Deps.Hilt.KAPT)

    // Truth
    testImplementation(Deps.Truth.TEST)

    // Modules
    implementation(project(Deps.Modules.DOMAIN))
}

kapt {
    correctErrorTypes = true
}
