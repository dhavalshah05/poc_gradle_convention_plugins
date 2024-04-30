import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import com.gradle.convention.debugImplementationExt
import com.gradle.convention.findVersionStringExt
import com.gradle.convention.implementationBomExt
import com.gradle.convention.implementationExt
import com.gradle.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class ComposeConventionPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        with(project) {
            val applicationExtension = try {
                extensions.getByType<ApplicationExtension>()
            } catch (e: Throwable) {
                null
            }

            val libraryExtension = try {
                extensions.getByType<LibraryExtension>()
            } catch (e: Throwable) {
                null
            }

            val commonExtensions = (applicationExtension as? CommonExtension<*, *, *, *, *>)
                ?: (libraryExtension as? CommonExtension<*, *, *, *, *>)
                ?: throw Exception("Not able to find either Application or Library extension")

            println("DEBUG_LOG: Application Extension: $applicationExtension")
            println("DEBUG_LOG: Library Extension: $libraryExtension")
            println("DEBUG_LOG: Common Extension: $commonExtensions")

            with(commonExtensions) {
                buildFeatures {
                    compose = true
                }

                composeOptions {
                    kotlinCompilerExtensionVersion = libs.findVersionStringExt("composeKotlinCompiler")
                }
            }

            dependencies {
                implementationBomExt("compose-bom")
                implementationExt("compose-material")
                debugImplementationExt("compose-ui-tooling")
                implementationExt("compose-ui-toolingPreview")
            }
        }
    }

}