package com.sorrowblue.comicviewer.domain.entity

import android.os.Parcelable
import android.util.Base64
import java.io.Serializable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed interface File : Parcelable, Serializable {
    val serverId: ServerId
    val name: String
    val parent: String
    val path: String
    val size: Long
    val lastModifier: Long


    fun base64Path() = Base64.encodeToString(path.encodeToByteArray(), Base64.URL_SAFE or Base64.NO_WRAP)
}
