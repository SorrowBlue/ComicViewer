package com.sorrowblue.comicviewer.feature.collection.add.navigation

import com.sorrowblue.cmpdestinations.annotation.NavGraph
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.feature.collection.add.BasicCollectionAdd
import kotlinx.serialization.Serializable

@NavGraph(startDestination = BasicCollectionAdd::class, destinations = [BasicCollectionAdd::class])
@Serializable
data class BasicCollectionAddNavGraph(val bookshelfId: BookshelfId, val path: String)
