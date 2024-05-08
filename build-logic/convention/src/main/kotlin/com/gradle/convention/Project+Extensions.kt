package com.gradle.convention

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.kotlin
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinTopLevelExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

internal val Project.libs
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

internal fun Project.implementationExt(dependencyNotation: String) {
    with(dependencies) {
        add("implementation", libs.findLibraryExt(dependencyNotation))
    }
}

internal fun Project.implementationBomExt(dependencyNotation: String) {
    with(dependencies) {
        add("implementation", platform(libs.findLibraryExt(dependencyNotation)))
    }
}

internal fun Project.debugImplementationExt(dependencyNotation: String) {
    with(dependencies) {
        add("debugImplementation", libs.findLibraryExt(dependencyNotation))
    }
}

internal fun Project.getCommonExtension(): CommonExtension<*, *, *, *, *, *> {
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

    return (applicationExtension as? CommonExtension<*, *, *, *, *, *>)
        ?: (libraryExtension as? CommonExtension<*, *, *, *, *, *>)
        ?: throw Exception("Not able to find neither Application nor Library extension")
}

private val javaVersion = JavaVersion.VERSION_17

internal fun Project.configureKotlin() {
    extensions.configure<KotlinTopLevelExtension> {
        jvmToolchain(javaVersion.toString().toInt())
    }

    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = javaVersion.toString()
        }
    }
}

internal fun Project.configureJavaAndroid() {
    getCommonExtension().compileOptions {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }
}

internal fun Project.configureJavaJvm() {
    extensions.configure<JavaPluginExtension> {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }
}
