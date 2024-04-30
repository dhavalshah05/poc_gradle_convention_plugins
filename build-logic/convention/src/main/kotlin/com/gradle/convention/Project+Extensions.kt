package com.gradle.convention

import com.android.build.api.dsl.AndroidResources
import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.BuildFeatures
import com.android.build.api.dsl.BuildType
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.DefaultConfig
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.dsl.ProductFlavor
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
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

internal fun Project.getCommonExtension(): CommonExtension<out BuildFeatures, out BuildType, out DefaultConfig, out ProductFlavor, out AndroidResources> {
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

    return (applicationExtension as? CommonExtension<*, *, *, *, *>)
        ?: (libraryExtension as? CommonExtension<*, *, *, *, *>)
        ?: throw Exception("Not able to find either Application or Library extension")
}

internal fun Project.configureKotlin() {
    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_17.toString()
        }
    }
}

internal fun Project.configureJavaAndroid() {
    getCommonExtension().compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

