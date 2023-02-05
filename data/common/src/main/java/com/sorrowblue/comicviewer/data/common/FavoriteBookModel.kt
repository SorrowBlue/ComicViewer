package com.sorrowblue.comicviewer.data.common

data class FavoriteBookModel(
    val id: FavoriteModelId,
    val serverModelId: ServerModelId,
    val filePath: String
)
