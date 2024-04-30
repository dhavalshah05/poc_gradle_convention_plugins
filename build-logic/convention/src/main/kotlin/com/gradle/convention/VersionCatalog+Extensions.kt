package com.gradle.convention

import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.provider.Provider

internal fun VersionCatalog.findVersionIntExt(alias: String): Int {
    return findVersion(alias).get().toString().toInt()
}

internal fun VersionCatalog.findVersionStringExt(alias: String): String {
    return findVersion(alias).get().toString()
}

internal fun VersionCatalog.findLibraryExt(alias: String): Provider<MinimalExternalModuleDependency> {
    return findLibrary(alias).get()
}