package com.sorrowblue.comicviewer.app

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Book
import androidx.compose.material.icons.twotone.Favorite
import androidx.compose.material.icons.twotone.LibraryBooks
import androidx.compose.material.icons.twotone.WatchLater
import androidx.compose.ui.graphics.vector.ImageVector

enum class MainScreenTab(
    val label: Int,
    val icon: ImageVector,
    val contentDescription: Int
) {
    Bookshelf(
        R.string.app_label_bookshelf,
        Icons.TwoTone.Book,
        R.string.app_label_bookshelf
    ),

    Favorite(
        R.string.app_label_favorite,
        Icons.TwoTone.Favorite,
        R.string.app_label_favorite
    ),

    Readlater(
        R.string.app_label_read_later,
        Icons.TwoTone.WatchLater,
        R.string.app_label_read_later
    ),

    Library(
        R.string.app_label_library,
        Icons.TwoTone.LibraryBooks,
        R.string.app_label_library
    ),
}
