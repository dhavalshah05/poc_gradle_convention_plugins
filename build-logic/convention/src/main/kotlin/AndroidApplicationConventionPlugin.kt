import com.android.build.api.dsl.ApplicationExtension
import com.gradle.convention.findVersionIntExt
import com.gradle.convention.findVersionStringExt
import com.gradle.convention.implementationExt
import com.gradle.convention.libs
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class AndroidApplicationConventionPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        with(project) {
            with(pluginManager) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
                apply("org.jetbrains.kotlin.plugin.serialization")
                apply("kotlin-parcelize")
            }

            extensions.configure<ApplicationExtension> {
                compileSdk = libs.findVersionIntExt("compileSdk")

                defaultConfig {
                    minSdk = libs.findVersionIntExt("minSdk")
                    targetSdk = libs.findVersionIntExt("targetSdk")
                    versionCode = libs.findVersionIntExt("versionCode")
                    versionName = libs.findVersionStringExt("versionName")

                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                }

                signingConfigs {
                    getByName("debug") {
                        storeFile = file("../keys/debug.keystore")
                        storePassword = "android"
                        keyAlias = "androiddebugkey"
                        keyPassword = "android"
                    }
                    create("release") {
                        storeFile = file("../keys/debug.keystore")
                        storePassword = "android"
                        keyAlias = "androiddebugkey"
                        keyPassword = "android"
                    }
                }

                buildTypes {
                    getByName("debug") {
                        isMinifyEnabled = false
                        isDebuggable = true
                        signingConfig = signingConfigs.getByName("debug")
                    }
                    getByName("release") {
                        isMinifyEnabled = true
                        isDebuggable = false
                        signingConfig = signingConfigs.getByName("release")
                        proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
                    }
                }

                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_17
                    targetCompatibility = JavaVersion.VERSION_17
                }

                buildFeatures {
                    buildConfig = true
                }
            }

            tasks.withType<KotlinCompile>().configureEach {
                kotlinOptions {
                    jvmTarget = JavaVersion.VERSION_17.toString()
                }
            }

            dependencies {
                // AndroidX
                implementationExt("androidX-coreKtx")
                implementationExt("androidX-appCompat")
                implementationExt("androidX-constraintLayout")
                implementationExt("androidX-lifecycle-viewModel")

                // Coroutine
                implementationExt("coroutines-core")
                implementationExt("coroutines-android")

                implementationExt("nitrozenAndroid")
            }
        }
    }
}