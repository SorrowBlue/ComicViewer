package com.sorrowblue.comicviewer.data.common

@JvmInline
value class ServerFileModel(val value: Pair<ServerModelId, FileModel>) {

    val serverModelId get() = value.first
    val fileModel get() = value.second
}
