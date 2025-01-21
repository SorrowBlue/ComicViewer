package com.sorrowblue.comicviewer.app.component

import androidx.compose.ui.graphics.vector.ImageVector
import com.sorrowblue.comicviewer.favorite.navigation.FavoriteNavGraph
import com.sorrowblue.comicviewer.feature.bookshelf.navgraph.BookshelfNavGraph
import com.sorrowblue.comicviewer.feature.readlater.navigation.ReadLaterNavGraph
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import comicviewer.composeapp.generated.resources.Res
import comicviewer.composeapp.generated.resources.app_label_bookshelf
import comicviewer.composeapp.generated.resources.app_label_favorite
import comicviewer.composeapp.generated.resources.app_label_library
import comicviewer.composeapp.generated.resources.app_label_read_later
import org.jetbrains.compose.resources.StringResource

enum class MainScreenTab(
    val navGraph: Any,
    val label: StringResource,
    val icon: ImageVector,
) {
    Bookshelf(BookshelfNavGraph, Res.string.app_label_bookshelf, ComicIcons.Book),
    Favorite(FavoriteNavGraph, Res.string.app_label_favorite, ComicIcons.Favorite),
    Readlater(ReadLaterNavGraph, Res.string.app_label_read_later, ComicIcons.WatchLater),
    Library(Unit, Res.string.app_label_library, ComicIcons.LibraryBooks),
}
