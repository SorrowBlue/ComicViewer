package com.sorrowblue.comicviewer.data.model.bookshelf

data class BookshelfModelId(val value: Int) {
    companion object
}

enum class FolderThumbnailOrderModel {
    NAME,
    MODIFIED,
    LAST_READ;
}
