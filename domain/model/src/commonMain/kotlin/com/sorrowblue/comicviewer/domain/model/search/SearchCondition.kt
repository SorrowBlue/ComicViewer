/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.search

import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import kotlinx.serialization.Serializable

/**
 * Search condition parameters for querying files.
 *
 * @property query Search keyword query.
 * @property range Target search scope.
 * @property period Modification date filter period.
 * @property sortType Sort order applied to search results.
 * @property showHidden Whether to include hidden files.
 */
@Serializable
data class SearchCondition(
    val query: String = "",
    val range: Range = Range.Bookshelf,
    val period: Period = Period.None,
    val sortType: SortType = SortType.Name(true),
    val showHidden: Boolean = false,
) {
    @Serializable
    sealed interface Range {
        @Serializable
        data object Bookshelf : Range

        @Serializable
        data class InFolder(val parent: String) : Range

        @Serializable
        data class SubFolder(val parent: String) : Range

        companion object {
            val entries by lazy { listOf(Bookshelf, InFolder(""), SubFolder("")) }
        }
    }

    @Serializable
    enum class Period {
        None,
        Hour24,
        Week1,
        Month1,
    }
}
