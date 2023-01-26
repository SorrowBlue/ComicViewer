package com.sorrowblue.comicviewer.domain.entity

import com.sorrowblue.comicviewer.domain.entity.server.ServerId

@JvmInline
value class FavoriteId(val value: Int)

data class Favorite(val id: FavoriteId, val name: String, val count: Int) {

    constructor(name: String) : this(FavoriteId(0), name, 0)
}

data class FavoriteBook(val id: FavoriteId, val serverId: ServerId, val path: String)
