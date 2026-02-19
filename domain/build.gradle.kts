plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id(BuildPlugins.HILT)
}

android {
    compileSdk = ConfigData.compileSdkVersion
    namespace = "app.expense.domain"

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
    // Hilt
    implementation(Deps.Hilt.HILT)
    kapt(Deps.Hilt.KAPT)

    // Coroutines
    implementation(Deps.Coroutines.DEP)

    // Junit
    testImplementation(Deps.JUnit.TEST)

    // Mockk
    testImplementation(Deps.Mockk.TEST)

    // Truth
    testImplementation(Deps.Truth.TEST)

    // Modules
    implementation(project(Deps.Modules.API))
}

kapt {
    correctErrorTypes = true
}
