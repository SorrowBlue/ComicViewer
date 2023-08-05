package com.sorrowblue.comicviewer.data.common.bookshelf

data class BookshelfModelId(val value: Int) {
    companion object
}

enum class ScanTypeModel {
    FULL, QUICK
}

enum class FolderThumbnailOrderModel {
    NAME,
    MODIFIED,
    LAST_READ;
}
