package com.sorrowblue.comicviewer.domain.model.settings.folder

import kotlinx.serialization.Serializable

/**
 * Represents the sorting type for file and folder lists. This sealed
 * interface provides different sorting strategies.
 */
@Serializable
sealed interface SortType {
    /** Indicates whether the sorting is ascending. */
    val isAsc: Boolean

    /**
     * Sort by name.
     *
     * @property isAsc Whether the sorting is ascending.
     */
    @Serializable
    data class Name(override val isAsc: Boolean) : SortType

    /**
     * Sort by date.
     *
     * @property isAsc Whether the sorting is ascending.
     */
    @Serializable
    data class Date(override val isAsc: Boolean) : SortType

    /**
     * Sort by size.
     *
     * @property isAsc Whether the sorting is ascending.
     */
    @Serializable
    data class Size(override val isAsc: Boolean) : SortType

    companion object {
        /** List of all available sort type entries. */
        val entries
            get() = listOf(
                Name(true),
                Name(false),
                Date(true),
                Date(false),
                Size(true),
                Size(false),
            )
    }
}
