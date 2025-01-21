package com.sorrowblue.comicviewer.app.navigation

import com.sorrowblue.comicviewer.favorite.navigation.FavoriteNavGraph
import com.sorrowblue.comicviewer.feature.book.Book
import com.sorrowblue.comicviewer.feature.bookshelf.navgraph.BookshelfNavGraph
import com.sorrowblue.comicviewer.feature.favorite.add.FavoriteAdd
import com.sorrowblue.comicviewer.feature.favorite.create.FavoriteCreate
import com.sorrowblue.comicviewer.feature.readlater.navigation.ReadLaterNavGraph
import com.sorrowblue.comicviewer.feature.search.navigation.SearchNavGraph
import com.sorrowblue.comicviewer.feature.tutorial.navigation.TutorialNavGraph
import com.sorrowblue.comicviewer.framework.annotation.DestinationInGraph
import com.sorrowblue.comicviewer.framework.annotation.NestedNavGraph
import kotlinx.serialization.Serializable

@Serializable
internal data object ComicViewerAppNavGraph

@com.sorrowblue.comicviewer.framework.annotation.NavGraph<ComicViewerAppNavGraph>(
    startDestination = BookshelfNavGraph::class,
    root = ComicViewerAppNavGraphImpl::class
)
internal class ComicViewerAppNavigation {

    @NestedNavGraph<BookshelfNavGraph>
    @NestedNavGraph<ReadLaterNavGraph>
    @NestedNavGraph<FavoriteNavGraph>
    @NestedNavGraph<SearchNavGraph>
    @NestedNavGraph<TutorialNavGraph>
    @DestinationInGraph<Book>
    @DestinationInGraph<FavoriteAdd>
    @DestinationInGraph<FavoriteCreate>
    companion object
}
