package com.sorrowblue.comicviewer.domain.entity.file

sealed interface Book : File {
    val cacheKey: String
    val lastPageRead: Int
    val totalPageCount: Int
    val lastReadTime: Long
}
