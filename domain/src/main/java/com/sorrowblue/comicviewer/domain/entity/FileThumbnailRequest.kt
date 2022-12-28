package com.sorrowblue.comicviewer.domain.entity

@JvmInline
value class FileThumbnailRequest(val value: Pair<Server, File>) {
    val server get() = value.first
    val file get() = value.second
}
