package com.sorrowblue.comicviewer.domain.entity.server

import android.os.Parcelable

sealed interface Server : Parcelable {
    val id: ServerId
    val displayName: String

}
