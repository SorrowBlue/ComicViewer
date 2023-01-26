package com.sorrowblue.comicviewer.domain.entity.server

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
@JvmInline
value class ServerId(val value: Int) : Parcelable
