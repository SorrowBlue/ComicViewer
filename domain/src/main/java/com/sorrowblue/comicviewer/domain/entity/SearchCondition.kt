package com.sorrowblue.comicviewer.domain.entity

data class SearchCondition(
    val query: String?,
    val range: Range,
    val period: Period
) {

    sealed class Range {
        data object BOOKSHELF : Range()
        data class IN_FOLDER(val parent: String) : Range()
        data class FOLDER_BELOW(val parent: String) : Range()
    }

    enum class Period {
        NONE,
        HOUR_24,
        WEEK_1,
        MONTH_1
    }
}
