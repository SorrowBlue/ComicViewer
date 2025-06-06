package com.sorrowblue.comicviewer.app.component

import androidx.compose.ui.graphics.vector.ImageVector
import com.sorrowblue.comicviewer.feature.bookshelf.navgraph.BookshelfNavGraph
import com.sorrowblue.comicviewer.feature.collection.navigation.CollectionNavGraph
import com.sorrowblue.comicviewer.feature.history.navigation.HistoryNavGraph
import com.sorrowblue.comicviewer.feature.readlater.navigation.ReadLaterNavGraph
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import comicviewer.composeapp.generated.resources.Res
import comicviewer.composeapp.generated.resources.app_label_bookshelf
import comicviewer.composeapp.generated.resources.app_label_collection
import comicviewer.composeapp.generated.resources.app_label_history
import comicviewer.composeapp.generated.resources.app_label_read_later
import org.jetbrains.compose.resources.StringResource

enum class MainScreenTab(
    val navGraph: Any,
    val label: StringResource,
    val icon: ImageVector,
) {
    Bookshelf(BookshelfNavGraph, Res.string.app_label_bookshelf, ComicIcons.Book),
    Collection(CollectionNavGraph, Res.string.app_label_collection, ComicIcons.CollectionsBookmark),
    Readlater(ReadLaterNavGraph, Res.string.app_label_read_later, ComicIcons.WatchLater),
    History(HistoryNavGraph, Res.string.app_label_history, ComicIcons.History),
}
