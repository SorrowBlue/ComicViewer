package com.sorrowblue.comicviewer.feature.collection.editor.navigation

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.file.PathString
import com.sorrowblue.comicviewer.framework.ui.navigation.ScreenKey
import kotlinx.serialization.Serializable

@Serializable
data class BasicCollectionCreateNavKey(
    val bookshelfId: BookshelfId = BookshelfId.Companion(),
    val path: PathString = "",
) : ScreenKey

@Serializable
data class BasicCollectionEditNavKey(val collectionId: CollectionId) : ScreenKey

@Serializable
data class SmartCollectionEditNavKey(val collectionId: CollectionId) : ScreenKey
