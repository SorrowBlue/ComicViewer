package com.sorrowblue.comicviewer.domain.model.settings.folder

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
sealed interface SortType : Parcelable {

    val isAsc: Boolean

    @Parcelize
    @Serializable
    data class Name(override val isAsc: Boolean) : SortType

    @Parcelize
    @Serializable
    data class Date(override val isAsc: Boolean) : SortType

    @Parcelize
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
