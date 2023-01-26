package com.sorrowblue.comicviewer.domain.request

import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.entity.server.ServerId

@JvmInline
value class FileThumbnailRequest(val value: Pair<ServerId, File>) {
    val serverId get() = value.first
    val file get() = value.second
}
