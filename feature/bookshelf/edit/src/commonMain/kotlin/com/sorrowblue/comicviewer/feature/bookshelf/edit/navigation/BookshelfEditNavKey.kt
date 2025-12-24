package com.sorrowblue.comicviewer.feature.bookshelf.edit.navigation

import com.sorrowblue.comicviewer.feature.bookshelf.edit.BookshelfEditType
import com.sorrowblue.comicviewer.framework.ui.navigation.ScreenKey
import kotlinx.serialization.Serializable

@Serializable
data class BookshelfEditNavKey(val type: BookshelfEditType) : ScreenKey

@Serializable
data object BookshelfSelectionNavKey : ScreenKey

@Serializable
internal data object BookshelfDiscardConfirmNavKey : ScreenKey
