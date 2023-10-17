package com.sorrowblue.comicviewer.domain.model.file

sealed interface IFolder : File {
    val count: Int
}
