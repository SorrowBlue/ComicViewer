package com.sorrowblue.comicviewer.data.common.bookshelf

import com.sorrowblue.comicviewer.data.common.FileModel

@JvmInline
value class BookshelfFileModel(val value: Pair<BookshelfModelId, FileModel>) {

    val bookshelfModelId get() = value.first
    val fileModel get() = value.second
}
