package com.sorrowblue.comicviewer.domain.entity.favorite

import com.sorrowblue.comicviewer.domain.entity.server.ServerId

data class FavoriteBook(val id: FavoriteId, val serverId: ServerId, val path: String)
