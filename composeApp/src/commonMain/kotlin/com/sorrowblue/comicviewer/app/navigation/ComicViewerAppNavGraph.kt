package com.sorrowblue.comicviewer.app.navigation

import com.sorrowblue.comicviewer.feature.book.Book
import com.sorrowblue.comicviewer.feature.bookshelf.navgraph.BookshelfNavGraph
import com.sorrowblue.comicviewer.feature.readlater.navigation.ReadLaterNavGraph
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
    @DestinationInGraph<Book>
    companion object
}
