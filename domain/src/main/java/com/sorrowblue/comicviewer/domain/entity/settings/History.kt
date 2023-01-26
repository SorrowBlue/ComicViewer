package com.sorrowblue.comicviewer.domain.entity.settings

import com.sorrowblue.comicviewer.domain.entity.server.ServerId
import kotlinx.serialization.Serializable

@Serializable
data class History(val serverId: ServerId? = null, val path: String? = null, val position: Int? = null)


