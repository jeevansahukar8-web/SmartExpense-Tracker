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

    // DataStore
    implementation(Deps.DataStore.ANDROID_PREFS)

    // Coroutines
    implementation(Deps.Coroutines.DEP)

    // TFLite
    implementation(Deps.TFLite.CORE)
    implementation(Deps.TFLite.SUPPORT)
    implementation(Deps.TFLite.TASK_TEXT)

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
    arguments {
        arg("dagger.fastInit", "enabled")
        arg("dagger.hilt.android.internal.disableAndroidSuperclassValidation", "true")
        arg("dagger.hilt.android.internal.projectType", "LIBRARY")
        arg("dagger.hilt.internal.useAggregatingRootProcessor", "true")
    }
}
