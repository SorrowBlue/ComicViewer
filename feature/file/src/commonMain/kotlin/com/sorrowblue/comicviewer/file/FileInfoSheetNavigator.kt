package com.sorrowblue.comicviewer.file

import com.sorrowblue.comicviewer.domain.model.file.File


sealed interface FileInfoSheetNavigator {
    data object Back : FileInfoSheetNavigator
    data class Favorite(val file: File) : FileInfoSheetNavigator
    data class OpenFolder(val file: File) : FileInfoSheetNavigator
}
