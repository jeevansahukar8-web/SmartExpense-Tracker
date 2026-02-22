/**
 * To define plugins
 */

private const val KOTLIN_VERSION = "1.9.22"

object BuildPlugins {
    private const val GRADLE_PLUGIN_VERSION = "8.3.0"

    const val ANDROID = "com.android.tools.build:gradle:$GRADLE_PLUGIN_VERSION"
    const val KOTLIN = "org.jetbrains.kotlin:kotlin-gradle-plugin:$KOTLIN_VERSION"
    const val HILT = "com.google.dagger.hilt.android"
    const val KSP = "com.google.devtools.ksp"
}

/**
 * To define dependencies
 */
object Deps {
    const val KOTLIN = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$KOTLIN_VERSION"

    object Core {
        const val DEPENDENCY = "androidx.core:core-ktx:1.12.0"
    }

    object Coroutines {
        const val DEP = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3"
    }

    object Material {
        const val DEPENDENCY = "com.google.android.material:material:1.11.0"
    }

    object Retrofit {
        private const val RETROFIT_VERSION = "2.9.0"

        const val RETROFIT = "com.squareup.retrofit2:retrofit:$RETROFIT_VERSION"
        const val GSON = "com.squareup.retrofit2:converter-gson:$RETROFIT_VERSION"
    }

    object Compose {
        private const val COMPOSE_VERSION = "1.6.1"

        const val UI = "androidx.compose.ui:ui:$COMPOSE_VERSION"
        const val MATERIAL3 = "androidx.compose.material3:material3:1.2.0"
        const val PREVIEW = "androidx.compose.ui:ui-tooling-preview:$COMPOSE_VERSION"
        const val CONSTRAINT = "androidx.constraintlayout:constraintlayout-compose:1.0.1"
        const val ACTIVITY = "androidx.activity:activity-compose:1.8.2"
        const val RUNTIME = "androidx.compose.runtime:runtime:$COMPOSE_VERSION"
        const val FOUNDATION = "androidx.compose.foundation:foundation:$COMPOSE_VERSION"
        const val VIEW_MODEL = "androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0"
        const val GOOGLE_FONTS = "androidx.compose.ui:ui-text-google-fonts:$COMPOSE_VERSION"
        const val DEBUG_TOOLING = "androidx.compose.ui:ui-tooling:$COMPOSE_VERSION"
        const val DEBUG_MANIFEST = "androidx.compose.ui:ui-test-manifest:$COMPOSE_VERSION"
        const val ANDROID_UI_TEST = "androidx.compose.ui:ui-test-junit4:$COMPOSE_VERSION"
    }

    object Room {
        private const val ROOM_VERSION = "2.6.1"

        const val RUNTIME = "androidx.room:room-runtime:$ROOM_VERSION"
        const val KTX = "androidx.room:room-ktx:$ROOM_VERSION"
        const val KAPT_COMPILER = "androidx.room:room-compiler:$ROOM_VERSION"
    }

    object JUnit {
        const val TEST = "junit:junit:4.13.2"
        const val ANDROID_TEST = "androidx.test.ext:junit:1.1.5"
    }

    object Mockk {
        const val TEST = "io.mockk:mockk:1.13.8"
    }

    object Truth {
        const val TEST = "com.google.truth:truth:1.1.3"
    }

    object Espresso {
        const val ANDROID_TEST = "androidx.test.espresso:espresso-core:3.5.1"
    }

    object Navigation {
        const val NAV_COMPOSE = "androidx.navigation:navigation-compose:2.7.6"
    }

    object Hilt {
        private const val HILT_VERSION = "2.51.1"
        private const val HILT_OTHER_JETPACK_VERSION = "1.1.0"

        const val HILT = "com.google.dagger:hilt-android:$HILT_VERSION"
        const val KAPT = "com.google.dagger:hilt-android-compiler:$HILT_VERSION"
        const val WORKER = "androidx.hilt:hilt-work:$HILT_OTHER_JETPACK_VERSION"
        const val KAPT_WORKER = "androidx.hilt:hilt-compiler:$HILT_OTHER_JETPACK_VERSION"
        const val NAVIGATION_COMPOSE =
            "androidx.hilt:hilt-navigation-compose:$HILT_OTHER_JETPACK_VERSION"
    }

    object WorkManager {
        const val RUNTIME = "androidx.work:work-runtime-ktx:2.9.0"
    }

    object DataStore {
        private const val DATA_STORE_VERSION = "1.0.0"

        const val PREFS = "androidx.datastore:datastore-preferences-core:$DATA_STORE_VERSION"
        const val ANDROID_PREFS = "androidx.datastore:datastore-preferences:$DATA_STORE_VERSION"
        const val CORE = "androidx.datastore:datastore:$DATA_STORE_VERSION"

    }

    object Accompanies {
        const val DEPS = "com.google.accompanist:accompanist-permissions:0.32.0"
    }

    object TFLite {
        private const val VERSION = "2.14.0"
        const val CORE = "org.tensorflow:tensorflow-lite:$VERSION"
        const val SUPPORT = "org.tensorflow:tensorflow-lite-support:0.4.4"
        const val METADATA = "org.tensorflow:tensorflow-lite-metadata:0.4.4"
        const val TASK_TEXT = "org.tensorflow:tensorflow-lite-task-text:0.4.4"
    }

    object Modules {
        const val PRESENTATION = ":presentation"
        const val DOMAIN = ":domain"
        const val API = ":api"
        const val APP = ":app"
    }
}
