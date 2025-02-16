package com.sorrowblue.comicviewer.domain.model.file

data class FileAttribute(
    val archive: Boolean = false,
    val compressed: Boolean = false,
    val directory: Boolean = false,
    val normal: Boolean = false,
    val readonly: Boolean = false,
    val system: Boolean = false,
    val temporary: Boolean = false,
    val sharedRead: Boolean = false,
    val hidden: Boolean = false,
    val volume: Boolean = false,
)
