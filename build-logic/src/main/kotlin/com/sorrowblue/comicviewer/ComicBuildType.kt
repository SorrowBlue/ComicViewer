package com.sorrowblue.comicviewer

enum class ComicBuildType(
    val isMinifyEnabled: Boolean,
    val applicationIdSuffix: String? = null,
) {
    DEBUG(false, ".debug"),
    RELEASE(true),
    INTERNAL(true),
    PRRELEASE(true, ".prelease")
}
