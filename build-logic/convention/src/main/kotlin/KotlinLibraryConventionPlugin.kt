import com.gradle.convention.configureJavaJvm
import com.gradle.convention.configureKotlin
import org.gradle.api.Plugin
import org.gradle.api.Project

class KotlinLibraryConventionPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        with(project) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.jvm")
            }

            configureJavaJvm()
            configureKotlin()
        }
    }
}