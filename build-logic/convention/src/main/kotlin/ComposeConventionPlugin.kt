import com.gradle.convention.debugImplementationExt
import com.gradle.convention.findVersionStringExt
import com.gradle.convention.getCommonExtension
import com.gradle.convention.implementationBomExt
import com.gradle.convention.implementationExt
import com.gradle.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class ComposeConventionPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        with(project) {
            val commonExtensions = getCommonExtension()

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