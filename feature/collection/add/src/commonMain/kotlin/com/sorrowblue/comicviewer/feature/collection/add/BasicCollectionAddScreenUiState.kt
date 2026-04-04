package com.sorrowblue.comicviewer.feature.collection.add

import com.sorrowblue.comicviewer.feature.collection.add.component.CollectionSort

internal data class BasicCollectionAddScreenUiState(
    val collectionSort: CollectionSort = CollectionSort.Recent,
)
