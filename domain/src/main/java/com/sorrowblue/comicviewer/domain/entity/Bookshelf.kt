package com.sorrowblue.comicviewer.domain.entity

import kotlinx.parcelize.Parcelize

@Parcelize
data class Bookshelf(
    override val serverId: ServerId,
    override val name: String,
    override val parent: String,
    override val path: String,
    override val size: Long,
    override val lastModifier: Long
) : File
