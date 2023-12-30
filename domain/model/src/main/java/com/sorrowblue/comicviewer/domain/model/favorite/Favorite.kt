package com.sorrowblue.comicviewer.domain.model.favorite

data class Favorite(val id: FavoriteId, val name: String, val count: Int, val exist: Boolean) {

    constructor(name: String) : this(FavoriteId(0), name, 0, false)
}
