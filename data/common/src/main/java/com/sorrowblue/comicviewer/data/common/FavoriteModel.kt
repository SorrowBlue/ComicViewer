package com.sorrowblue.comicviewer.data.common

data class FavoriteModel(val id: FavoriteModelId, val name: String, val count: Int) {
    constructor(title: String) : this(FavoriteModelId(0), title, 0)
}
