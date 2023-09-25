package com.sorrowblue.comicviewer.app

import androidx.compose.ui.graphics.vector.ImageVector
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons

enum class MainScreenTab(
    val label: Int,
    val icon: ImageVector,
    val contentDescription: Int,
) {
    Bookshelf(
        R.string.app_label_bookshelf,
        ComicIcons.Book,
        R.string.app_label_bookshelf
    ),

    Favorite(
        R.string.app_label_favorite,
        ComicIcons.Favorite,
        R.string.app_label_favorite
    ),

    Readlater(
        R.string.app_label_read_later,
        ComicIcons.WatchLater,
        R.string.app_label_read_later
    ),

    Library(
        R.string.app_label_library,
        ComicIcons.LibraryBooks,
        R.string.app_label_library
    ),
}
