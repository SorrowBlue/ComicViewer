package com.sorrowblue.comicviewer.domain.model.file

sealed interface Book : File {
    val lastPageRead: Int
    val totalPageCount: Int
    val lastReadTime: Long
}
