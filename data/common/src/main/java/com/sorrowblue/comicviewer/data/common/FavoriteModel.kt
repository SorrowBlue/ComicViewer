package com.sorrowblue.comicviewer.data.common

@JvmInline
value class FavoriteModelId(val value: Int)

data class FavoriteModel(val id: FavoriteModelId, val name: String, val count: Int) {
    constructor(title: String) : this(FavoriteModelId(0), title, 0)
}

data class FavoriteBookModel(
    val id: FavoriteModelId,
    val serverModelId: ServerModelId,
    val filePath: String
)
