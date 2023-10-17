package com.sorrowblue.comicviewer.domain.model.file

sealed interface Book : File {
    val cacheKey: String
    val lastPageRead: Int
    val totalPageCount: Int
    val lastReadTime: Long
}
