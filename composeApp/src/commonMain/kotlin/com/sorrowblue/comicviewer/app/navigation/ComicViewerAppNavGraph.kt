package com.sorrowblue.comicviewer.app.navigation

import com.sorrowblue.cmpdestinations.annotation.DestinationInGraph
import com.sorrowblue.cmpdestinations.annotation.NavGraph
import com.sorrowblue.cmpdestinations.annotation.NestedNavGraph
import com.sorrowblue.comicviewer.favorite.navigation.FavoriteNavGraph
import com.sorrowblue.comicviewer.feature.book.navigation.BookNavGraph
import com.sorrowblue.comicviewer.feature.bookshelf.navgraph.BookshelfNavGraph
import com.sorrowblue.comicviewer.feature.favorite.add.FavoriteAdd
import com.sorrowblue.comicviewer.feature.favorite.create.FavoriteCreate
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
    @NestedNavGraph<FavoriteNavGraph>
    @NestedNavGraph<SearchNavGraph>
    @NestedNavGraph<SettingsNavGraph>
    @NestedNavGraph<HistoryNavGraph>
    @NestedNavGraph<BookNavGraph>
    @DestinationInGraph<FavoriteAdd>
    @DestinationInGraph<FavoriteCreate>
    @DestinationInGraph<SortTypeSelect>
    object Include
}
