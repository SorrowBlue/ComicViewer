package com.sorrowblue.comicviewer.domain.entity

import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.domain.entity.server.Server

@JvmInline
value class ServerFolder(val value: Pair<Server, Folder>) {
    val server get() = value.first
    val bookshelf get() = value.second
}
