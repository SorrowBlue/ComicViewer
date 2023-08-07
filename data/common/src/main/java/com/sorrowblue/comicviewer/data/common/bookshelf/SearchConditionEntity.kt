package com.sorrowblue.comicviewer.data.common.bookshelf

class SearchConditionEntity(
    val query: String,
    val range: Range,
    val period: Period,
    val order: Order,
    val sort: Sort
) {

    companion object {
        const val NO_QUERY = ""
    }

    sealed class Range {
        data object BOOKSHELF : Range()
        data class InFolder(val parent: String) : Range()
        data class FolderBelow(val parent: String) : Range()
    }

    enum class Period {
        NONE,
        HOUR_24,
        WEEK_1,
        MONTH_1
    }

    enum class Order {
        NAME, DATE, SIZE
    }

    enum class Sort {
        ASC, DESC
    }
}
