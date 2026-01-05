package com.sorrowblue.comicviewer.feature.book.nav

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.file.PathString
import com.sorrowblue.comicviewer.framework.ui.navigation.ScreenKey
import kotlinx.serialization.Serializable

@Serializable
data class BookNavKey(
    val bookshelfId: BookshelfId,
    val path: PathString,
    val name: String,
    val collectionId: CollectionId = CollectionId(),
) : ScreenKey
