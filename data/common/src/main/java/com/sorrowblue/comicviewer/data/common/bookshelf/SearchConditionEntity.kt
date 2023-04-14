package com.sorrowblue.comicviewer.data.common.bookshelf

class SearchConditionEntity(
    val query: String?,
    val range: Range,
    val period: Period,
) {

    sealed class Range {
        object BOOKSHELF : Range()
        data class IN_FOLDER(val parent: String) : Range()
        data class FOLDER_BELOW(val parent: String) : Range()
    }

    enum class Period {
        NONE,
        HOUR_24,
        WEEK_1,
        MONTH_1
    }

    companion object
}
