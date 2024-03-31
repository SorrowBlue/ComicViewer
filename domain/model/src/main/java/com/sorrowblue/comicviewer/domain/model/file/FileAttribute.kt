package com.sorrowblue.comicviewer.domain.model.file

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FileAttribute(
    val archive: Boolean,
    val compressed: Boolean,
    val directory: Boolean,
    val normal: Boolean,
    val readonly: Boolean,
    val system: Boolean,
    val temporary: Boolean,
    val sharedRead: Boolean,
    val hidden: Boolean,
    val volume: Boolean,
) : Parcelable
