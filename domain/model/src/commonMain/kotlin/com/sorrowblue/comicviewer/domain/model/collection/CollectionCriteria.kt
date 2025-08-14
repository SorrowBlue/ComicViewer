package com.sorrowblue.comicviewer.domain.model.collection

data class CollectionCriteria(
    val type: CollectionType = CollectionType.All,
    val recent: Boolean = false,
)
