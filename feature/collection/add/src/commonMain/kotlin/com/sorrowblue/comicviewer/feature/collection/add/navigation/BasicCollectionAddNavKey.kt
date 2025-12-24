package com.sorrowblue.comicviewer.feature.collection.add.navigation

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.PathString
import com.sorrowblue.comicviewer.framework.ui.navigation.ScreenKey
import kotlinx.serialization.Serializable

@Serializable
data class BasicCollectionAddNavKey(val bookshelfId: BookshelfId, val path: PathString) : ScreenKey
