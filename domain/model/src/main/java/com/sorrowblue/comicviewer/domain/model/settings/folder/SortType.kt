package com.sorrowblue.comicviewer.domain.model.settings.folder

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
sealed interface SortType : Parcelable {

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

    companion object {
        val entries
            get() = listOf(
                NAME(true),
                NAME(false),
                DATE(true),
                DATE(false),
                SIZE(true),
                SIZE(false)
            )
    }
}
