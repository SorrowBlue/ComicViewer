package com.sorrowblue.comicviewer.domain.model.favorite

data class Favorite(
    val id: FavoriteId,
    val name: String,
    val count: Int,
    val exist: Boolean,
    val addedDateTime: Long,
) {

    constructor(name: String) : this(
        id = FavoriteId(),
        name = name,
        count = 0,
        exist = false,
        addedDateTime = 0
    )
}
