package com.sorrowblue.comicviewer.domain.model

import com.sorrowblue.comicviewer.domain.entity.ServerId
import kotlinx.serialization.Serializable

@Serializable
data class History(val serverId: ServerId? = null, val path: String? = null, val position: Int? = null)


