package com.sorrowblue.comicviewer.domain.model.settings.folder

import kotlinx.serialization.Serializable

@Serializable
sealed interface SortType {

    val isAsc: Boolean

    @Serializable
    data class Name(override val isAsc: Boolean) : SortType

    @Serializable
    data class Date(override val isAsc: Boolean) : SortType

    @Serializable
    data class Size(override val isAsc: Boolean) : SortType

    companion object {
        val entries
            get() = listOf(
                Name(true),
                Name(false),
                Date(true),
                Date(false),
                Size(true),
                Size(false)
            )
    }
}
