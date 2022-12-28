package com.sorrowblue.comicviewer.domain.entity

sealed interface Book : File {
    val cacheKey: String
    val lastPageRead: Int
    val totalPageCount: Int
    val lastReadTime: Long
}
