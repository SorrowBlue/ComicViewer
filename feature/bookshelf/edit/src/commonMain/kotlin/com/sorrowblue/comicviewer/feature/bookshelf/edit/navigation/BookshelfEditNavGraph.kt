package com.sorrowblue.comicviewer.feature.bookshelf.edit.navigation

import com.sorrowblue.cmpdestinations.annotation.NavGraph
import com.sorrowblue.comicviewer.feature.bookshelf.edit.BookshelfEdit
import com.sorrowblue.comicviewer.feature.bookshelf.edit.BookshelfEditMode
import kotlinx.serialization.Serializable

@Serializable
@NavGraph(startDestination = BookshelfEdit::class, destinations = [BookshelfEdit::class])
data class BookshelfEditNavGraph(val editMode: BookshelfEditMode)
