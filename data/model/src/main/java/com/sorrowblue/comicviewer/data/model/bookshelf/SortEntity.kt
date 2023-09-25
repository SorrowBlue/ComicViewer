package com.sorrowblue.comicviewer.data.model.bookshelf

sealed class SortEntity(open val isAsc: Boolean) {
    data class NAME(override val isAsc: Boolean) : SortEntity(isAsc)
    data class DATE(override val isAsc: Boolean) : SortEntity(isAsc)
    data class SIZE(override val isAsc: Boolean) : SortEntity(isAsc)

    companion object
}
