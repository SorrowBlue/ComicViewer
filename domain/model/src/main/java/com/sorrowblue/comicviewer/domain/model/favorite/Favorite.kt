package com.sorrowblue.comicviewer.domain.model.favorite

data class Favorite(val id: FavoriteId, val name: String, val count: Int) {

    constructor(name: String) : this(FavoriteId(0), name, 0)
}
