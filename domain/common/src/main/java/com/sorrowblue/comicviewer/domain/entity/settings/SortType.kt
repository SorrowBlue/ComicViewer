package com.sorrowblue.comicviewer.domain.entity.settings

import kotlinx.serialization.Serializable

@Serializable
sealed interface SortType {

    val isAsc: Boolean


    fun copy2(isAsc: Boolean): SortType {
        return when (this) {
            is DATE -> copy(isAsc)
            is NAME -> copy(isAsc)
            is SIZE -> copy(isAsc)
        }
    }

    @Serializable
    data class NAME(override val isAsc: Boolean) : SortType

    @Serializable
    data class DATE(override val isAsc: Boolean) : SortType

    @Serializable
    data class SIZE(override val isAsc: Boolean) : SortType
}
