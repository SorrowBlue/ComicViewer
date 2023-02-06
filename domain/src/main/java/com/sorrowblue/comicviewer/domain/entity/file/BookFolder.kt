package com.sorrowblue.comicviewer.domain.entity.file

import com.sorrowblue.comicviewer.domain.entity.server.ServerId

data class BookFolder(
    override val serverId: ServerId,
    override val name: String,
    override val parent: String,
    override val path: String,
    override val size: Long,
    override val lastModifier: Long,
    override val cacheKey: String,
    override val lastPageRead: Int,
    override val totalPageCount: Int,
    override val lastReadTime: Long,
) : Book
