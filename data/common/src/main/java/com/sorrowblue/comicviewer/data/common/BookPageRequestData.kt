package com.sorrowblue.comicviewer.data.common

@JvmInline
value class BookPageRequestData(val value: Triple<ServerModel, FileModel, Int>) {

    val serverModel get() = value.first
    val fileModel get() = value.second
    val pageIndex get() = value.third
}
