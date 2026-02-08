package com.sorrowblue.comicviewer.data.storage.smb

data class IosSmbFile(
    val path: String,
    val name: String,
    val isDirectory: Boolean,
    val fileSize: Long,
    val contentModificationDate: Long,
)
