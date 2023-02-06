package com.sorrowblue.comicviewer.data.common

import com.sorrowblue.comicviewer.data.common.bookshelf.BookshelfModelId

data class SimpleFileModel(
    val path: String,
    val bookshelfModelId: BookshelfModelId,
    val name: String,
    val parent: String,
    val size: Long,
    val lastModifier: Long,
    val type: FileModel,
    val sortIndex: Int
)
