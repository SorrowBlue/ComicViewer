package com.sorrowblue.comicviewer.domain.entity

import com.sorrowblue.comicviewer.domain.entity.server.ServerId

data class ReadLater(val serverId: ServerId, val path: String)
