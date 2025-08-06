package com.sorrowblue.comicviewer.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.sorrowblue.comicviewer.feature.bookshelf.navgraph.BookshelfNavGraph
import com.sorrowblue.comicviewer.feature.collection.navigation.CollectionNavGraph
import com.sorrowblue.comicviewer.feature.history.navigation.HistoryNavGraph
import com.sorrowblue.comicviewer.feature.readlater.navigation.ReadLaterNavGraph
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.navigation.NavItem
import comicviewer.composeapp.generated.resources.Res
import comicviewer.composeapp.generated.resources.app_label_bookshelf
import comicviewer.composeapp.generated.resources.app_label_collection
import comicviewer.composeapp.generated.resources.app_label_history
import comicviewer.composeapp.generated.resources.app_label_read_later
import org.jetbrains.compose.resources.stringResource

internal sealed class AppNavItem(
    val navGraph: Any,
    override val icon: ImageVector,
) : NavItem {
    data object Bookshelf : AppNavItem(
        BookshelfNavGraph,
        ComicIcons.Book
    ) {
        override val title: String
            @Composable
            get() = stringResource(Res.string.app_label_bookshelf)
    }

    data object Collection : AppNavItem(CollectionNavGraph, ComicIcons.CollectionsBookmark) {
        override val title: String
            @Composable
            get() = stringResource(Res.string.app_label_collection)
    }

    data object Readlater : AppNavItem(ReadLaterNavGraph, ComicIcons.WatchLater) {
        override val title: String
            @Composable
            get() = stringResource(Res.string.app_label_read_later)
    }

    data object History : AppNavItem(HistoryNavGraph, ComicIcons.History) {
        override val title: String
            @Composable
            get() = stringResource(Res.string.app_label_history)
    }

    companion object {
        val entries = listOf(Bookshelf, Collection, Readlater, History)
    }
}
