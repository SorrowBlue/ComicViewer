package com.sorrowblue.comicviewer.domain.model

import com.sorrowblue.comicviewer.domain.model.file.File

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect object SortUtil {

    val compareFile: Comparator<File>

    val compareName: Comparator<String>

    fun filter(file: File, supportExtensions: List<String>): Boolean

    fun sortedIndex(list: List<File>): List<File>
}

val SUPPORTED_IMAGE = setOf(
    "bmp",
    "gif",
    "jpg",
    "jpeg",
    "png",
    "webp",
    "heic",
    "heif"
)
