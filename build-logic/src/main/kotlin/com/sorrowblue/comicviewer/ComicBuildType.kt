package com.sorrowblue.comicviewer

enum class ComicBuildType(
    val isMinifyEnabled: Boolean,
    val isShrinkResources: Boolean,
    val applicationIdSuffix: String? = null,
) {
    DEBUG(false, false, ".debug"),
    RELEASE(true, true),
    INTERNAL(true, true),
    PRERELEASE(true, true, ".prerelease"),
    ;

    val display get() = this.name.lowercase()
}
