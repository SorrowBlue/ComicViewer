package com.sorrowblue.comicviewer.app.navigation

import com.sorrowblue.cmpdestinations.annotation.DestinationInGraph
import com.sorrowblue.cmpdestinations.annotation.NavGraph
import com.sorrowblue.cmpdestinations.annotation.NestedNavGraph
import com.sorrowblue.comicviewer.feature.book.navigation.BookNavGraph
import com.sorrowblue.comicviewer.feature.bookshelf.navgraph.BookshelfNavGraph
import com.sorrowblue.comicviewer.feature.collection.navigation.CollectionNavGraph
import com.sorrowblue.comicviewer.feature.history.navigation.HistoryNavGraph
import com.sorrowblue.comicviewer.feature.readlater.navigation.ReadLaterNavGraph
import com.sorrowblue.comicviewer.feature.search.navigation.SearchNavGraph
import com.sorrowblue.comicviewer.feature.settings.navigation.SettingsNavGraph
import com.sorrowblue.comicviewer.folder.SortTypeSelect
import kotlinx.serialization.Serializable

@NavGraph(startDestination = BookshelfNavGraph::class, root = ComicViewerAppNavGraphImpl::class)
@Serializable
internal data object ComicViewerAppNavGraph {
    @NestedNavGraph<BookshelfNavGraph>
    @NestedNavGraph<ReadLaterNavGraph>
    @NestedNavGraph<CollectionNavGraph>
    @NestedNavGraph<SearchNavGraph>
    @NestedNavGraph<SettingsNavGraph>
    @NestedNavGraph<HistoryNavGraph>
    @NestedNavGraph<BookNavGraph>
    @DestinationInGraph<SortTypeSelect>
    object Include
}
