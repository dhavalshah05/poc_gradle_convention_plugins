import com.android.build.api.dsl.LibraryExtension
import com.gradle.convention.configureJavaAndroid
import com.gradle.convention.configureKotlin
import com.gradle.convention.findVersionIntExt
import com.gradle.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidLibraryConventionPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        with(project) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
                apply("org.jetbrains.kotlin.plugin.serialization")
                apply("kotlin-parcelize")
            }

            configureSdkVersions()

            configureJavaAndroid()
            configureKotlin()
        }
    }

    private fun Project.configureSdkVersions() {
        extensions.configure<LibraryExtension> {
            compileSdk = libs.findVersionIntExt("compileSdk")

            defaultConfig {
                minSdk = libs.findVersionIntExt("minSdk")
            }
        }
    }
}