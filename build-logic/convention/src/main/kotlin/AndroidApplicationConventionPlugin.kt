import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.gradle.AbstractAppExtension
import com.android.build.gradle.internal.core.MergedFlavor
import com.gradle.convention.configureJavaAndroid
import com.gradle.convention.configureKotlin
import com.gradle.convention.findVersionIntExt
import com.gradle.convention.findVersionStringExt
import com.gradle.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType

class AndroidApplicationConventionPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        with(project) {
            with(pluginManager) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
                apply("org.jetbrains.kotlin.plugin.serialization")
                apply("kotlin-parcelize")
            }

            configureSdkVersionsAndReleaseCodes()
            configureBuildFeatures()

            createSigningConfigs()
            createProductFlavors()

            configureBuildVariants()
            configureSigningConfig()
            configureApplicationId()
            configureProperties()

            configureJavaAndroid()
            configureKotlin()
        }
    }

    private fun Project.configureSdkVersionsAndReleaseCodes() {
        extensions.configure<ApplicationExtension> {
            compileSdk = libs.findVersionIntExt("compileSdk")

            defaultConfig {
                minSdk = libs.findVersionIntExt("minSdk")
                targetSdk = libs.findVersionIntExt("targetSdk")
                versionCode = libs.findVersionIntExt("versionCode")
                versionName = libs.findVersionStringExt("versionName")
            }
        }
    }

    private fun Project.configureBuildFeatures() {
        extensions.configure<ApplicationExtension> {
            buildFeatures {
                buildConfig = true
            }
        }
    }

    private fun Project.createSigningConfigs() {
        extensions.configure<ApplicationExtension> {
            signingConfigs {
                create(SIGNING_KEY_DEBUG) {
                    storeFile = file("../keys/debug.keystore")
                    storePassword = "android"
                    keyAlias = "androiddebugkey"
                    keyPassword = "android"
                }

                create(SIGNING_KEY_RELEASE) {
                    storeFile = file("../keys/debug.keystore")
                    storePassword = "android"
                    keyAlias = "androiddebugkey"
                    keyPassword = "android"
                }
            }
        }
    }

    private fun Project.configureBuildVariants() {
        extensions.configure<ApplicationExtension> {
            buildTypes {
                getByName(BUILD_TYPE_DEBUG) {
                    isMinifyEnabled = false
                    isDebuggable = true
                    versionNameSuffix = ".$BUILD_TYPE_DEBUG"
                    signingConfig = signingConfigs.getByName(SIGNING_KEY_DEBUG)
                }

                getByName(BUILD_TYPE_RELEASE) {
                    isMinifyEnabled = true
                    isDebuggable = false
                    proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
                }
            }
        }
    }

    private fun Project.createProductFlavors() {
        extensions.configure<ApplicationExtension> {
            flavorDimensions.add("app")
            productFlavors {
                create(FLAVOR_DEVELOPMENT) {
                    buildConfigField("String", "API_BASE_URL", "\"DEVELOPMENT_API_URL\"")
                    versionNameSuffix = ".$FLAVOR_DEVELOPMENT"
                    isDefault = true
                }

                create(FLAVOR_PRODUCTION) {
                    buildConfigField("String", "API_BASE_URL", "\"PRODUCTION_API_URL\"")
                }
            }
        }
    }

    private fun Project.configureSigningConfig() {
        extensions.configure<ApplicationExtension> {
            extensions.configure<ApplicationAndroidComponentsExtension> {
                onVariants { variant ->
                    val isProductionReleaseBuild = variant.buildType == BUILD_TYPE_RELEASE && variant.flavorName == FLAVOR_PRODUCTION
                    val signingConfigName = if (isProductionReleaseBuild) SIGNING_KEY_RELEASE else SIGNING_KEY_DEBUG

                    @Suppress("UnstableApiUsage")
                    variant.signingConfig.setConfig(signingConfigs.getByName(signingConfigName))
                }
            }
        }
    }

    private fun Project.configureApplicationId() {
        val abstractAppExtension = extensions.getByType<AbstractAppExtension>()
        abstractAppExtension.applicationVariants.configureEach {
            val variant = this
            val mergedFlavor = (variant.mergedFlavor as MergedFlavor)
            val isProductionReleaseBuild = variant.buildType.name == BUILD_TYPE_RELEASE && variant.productFlavors[0].name == FLAVOR_PRODUCTION

            if (!isProductionReleaseBuild) {
                mergedFlavor.setApplicationId("${variant.applicationId}.dev")
            }
        }
    }

    private fun Project.configureProperties() {
        val abstractAppExtension = extensions.getByType<AbstractAppExtension>()
        abstractAppExtension.applicationVariants.configureEach {
            //val variant = this
            //val mergedFlavor = (variant.mergedFlavor as MergedFlavor)
            //val isProductionReleaseBuild = variant.buildType.name == BUILD_TYPE_RELEASE && variant.productFlavors[0].name == FLAVOR_PRODUCTION

            //variant.buildConfigField("String", "API_KEY", "\"${properties["API_KEY"]}\"")
            //variant.resValue("String", "API_KEY", "${API_KEY}")
            //mergedFlavor.manifestPlaceholders["GOOGLE_KEY"] = "${GOOGLE_KEY}"
        }
    }

    companion object {

        private const val SIGNING_KEY_DEBUG = "debugKey"
        private const val SIGNING_KEY_RELEASE = "releaseKey"

        private const val BUILD_TYPE_DEBUG = "debug"
        private const val BUILD_TYPE_RELEASE = "release"

        private const val FLAVOR_DEVELOPMENT = "dev"
        private const val FLAVOR_PRODUCTION = "prod"
    }
}