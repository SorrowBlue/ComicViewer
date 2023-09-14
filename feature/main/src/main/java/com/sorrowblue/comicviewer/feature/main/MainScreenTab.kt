package com.sorrowblue.comicviewer.feature.main

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

    Bookshelf(R.string.main_label_bookshelf, Icons.TwoTone.Book, R.string.main_label_bookshelf),
    Favorite(R.string.main_label_favorite, Icons.TwoTone.Favorite, R.string.main_label_favorite),
    Readlater(
        R.string.main_label_read_later,
        Icons.TwoTone.WatchLater,
        R.string.main_label_read_later
    ),
    Library(R.string.main_label_library, Icons.TwoTone.LibraryBooks, R.string.main_label_library),

}
