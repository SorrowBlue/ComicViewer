package com.sorrowblue.comicviewer.domain.model

import com.sorrowblue.comicviewer.domain.model.file.File

expect object SortUtil {

    val compareFile: Comparator<File>

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
