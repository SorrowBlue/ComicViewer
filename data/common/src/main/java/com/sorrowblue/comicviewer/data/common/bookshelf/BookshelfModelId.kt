package com.sorrowblue.comicviewer.data.common.bookshelf

data class BookshelfModelId(val value: Int)

enum class ScanTypeModel {
    FULL, QUICK
}

enum class FolderThumbnailOrderModel {
    NAME,
    MODIFIED,
    LAST_READ;
}

sealed class SortType(open val isAsc: Boolean) {
    data class NAME(override val isAsc: Boolean) : SortType(isAsc)
    data class DATE(override val isAsc: Boolean) : SortType(isAsc)
    data class SIZE(override val isAsc: Boolean) : SortType(isAsc)
}

enum class OrderModel {
    ASC,
    DESC,
}
