package com.sorrowblue.comicviewer.data.common

@JvmInline
value class BookPageRequestData(val value: Pair<FileModel, Int>) {

    val fileModel get() = value.first
    val pageIndex get() = value.second
}
