package com.gradle.convention

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

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