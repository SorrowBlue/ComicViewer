package com.sorrowblue.comicviewer

enum class ComicBuildType(
    val isMinifyEnabled: Boolean,
    val isShrinkResources: Boolean,
    val applicationIdSuffix: String? = null,
) {
    DEBUG(false, false),
    RELEASE(true, true),
    INTERNAL(true, true),
    PRERELEASE(true, true, ".prerelease")
}
