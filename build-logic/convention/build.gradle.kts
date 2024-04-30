plugins {
    `kotlin-dsl`
}

group = "com.gradle.convention"

dependencies {
    compileOnly(libs.gradlePlugin.android)
    compileOnly(libs.gradlePlugin.kotlin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "convention.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("compose") {
            id = "convention.compose"
            implementationClass = "ComposeConventionPlugin"
        }
    }
}