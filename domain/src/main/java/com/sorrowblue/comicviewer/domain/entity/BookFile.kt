package com.sorrowblue.comicviewer.domain.entity

import kotlinx.parcelize.Parcelize

@Parcelize
data class BookFile(
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
