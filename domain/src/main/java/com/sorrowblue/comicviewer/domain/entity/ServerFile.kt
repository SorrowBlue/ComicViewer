package com.sorrowblue.comicviewer.domain.entity

import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.entity.server.Server

@JvmInline
value class ServerFile(val value: Pair<Server, File>) {
    val server get() = value.first
    val file get() = value.second
}
