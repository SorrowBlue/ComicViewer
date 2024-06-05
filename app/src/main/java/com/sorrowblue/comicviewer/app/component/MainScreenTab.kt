package com.sorrowblue.comicviewer.app.component

import androidx.compose.ui.graphics.vector.ImageVector
import com.ramcosta.composedestinations.spec.NavGraphSpec
import com.sorrowblue.comicviewer.app.R
import com.sorrowblue.comicviewer.feature.bookshelf.navgraphs.BookshelfNavGraph
import com.sorrowblue.comicviewer.feature.favorite.navgraphs.FavoriteNavGraph
import com.sorrowblue.comicviewer.feature.library.navgraphs.LibraryNavGraph
import com.sorrowblue.comicviewer.feature.readlater.navgraphs.ReadLaterNavGraph
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons

enum class MainScreenTab(
    val navGraph: NavGraphSpec,
    val label: Int,
    val icon: ImageVector,
    val contentDescription: Int,
) {
    Bookshelf(
        BookshelfNavGraph,
        R.string.app_label_bookshelf,
        ComicIcons.Book,
        R.string.app_label_bookshelf
    ),

    Favorite(
        FavoriteNavGraph,
        R.string.app_label_favorite,
        ComicIcons.Favorite,
        R.string.app_label_favorite
    ),

    Readlater(
        ReadLaterNavGraph,
        R.string.app_label_read_later,
        ComicIcons.WatchLater,
        R.string.app_label_read_later
    ),

    Library(
        LibraryNavGraph,
        R.string.app_label_library,
        ComicIcons.LibraryBooks,
        R.string.app_label_library
    ),
}
