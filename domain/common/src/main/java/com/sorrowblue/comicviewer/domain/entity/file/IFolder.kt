package com.sorrowblue.comicviewer.domain.entity.file

sealed interface IFolder : File {
    val count: Int
}
