package com.sorrowblue.comicviewer.data.common

@JvmInline
value class ServerFileModel(val value: Pair<ServerModel, FileModel>) {

    val server get() = value.first
    val file get() = value.second
}
