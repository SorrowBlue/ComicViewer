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
    /**
     * Represents the directory scope of a search query.
     */
    @Serializable
    sealed interface Range {
        /** Search across the entire bookshelf. */
        @Serializable
        data object Bookshelf : Range

        /**
         * Search only direct children of the specified [parent] folder.
         *
         * @property parent The parent folder path.
         */
        @Serializable
        data class InFolder(val parent: String) : Range

        /**
         * Search within the specified [parent] folder and all of its subfolders.
         *
         * @property parent The ancestor folder path.
         */
        @Serializable
        data class SubFolder(val parent: String) : Range

        companion object {
            /** Default list of available search range options. */
            val entries by lazy { listOf(Bookshelf, InFolder(""), SubFolder("")) }
        }
    }

    /**
     * Represents a modification time filter period for search queries.
     */
    @Serializable
    enum class Period {
        /** No time limit. */
        None,

        /** Modified within the last 24 hours. */
        Hour24,

        /** Modified within the last 7 days. */
        Week1,

        /** Modified within the last 30 days. */
        Month1,
    }
}
