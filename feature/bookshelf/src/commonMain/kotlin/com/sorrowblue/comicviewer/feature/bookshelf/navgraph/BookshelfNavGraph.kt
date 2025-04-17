package com.sorrowblue.comicviewer.feature.bookshelf.navgraph

import com.sorrowblue.cmpdestinations.annotation.DestinationInGraph
import com.sorrowblue.cmpdestinations.annotation.NavGraph
import com.sorrowblue.cmpdestinations.annotation.NestedNavGraph
import com.sorrowblue.comicviewer.feature.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.feature.bookshelf.BookshelfFolder
import com.sorrowblue.comicviewer.feature.bookshelf.edit.navigation.BookshelfEditNavGraph
import com.sorrowblue.comicviewer.feature.bookshelf.info.delete.BookshelfDelete
import com.sorrowblue.comicviewer.feature.bookshelf.selection.BookshelfSelection
import com.sorrowblue.comicviewer.framework.ui.navigation.TabDisplayRoute
import kotlin.reflect.KClass
import kotlinx.serialization.Serializable
import org.koin.core.annotation.Singleton

@Serializable
@NavGraph(startDestination = Bookshelf::class, transitions = BookshelfNavGraphTransitions::class)
data object BookshelfNavGraph {

    @DestinationInGraph<Bookshelf>
    @DestinationInGraph<BookshelfDelete>
    @DestinationInGraph<BookshelfFolder>
    @DestinationInGraph<BookshelfSelection>
    @NestedNavGraph<BookshelfEditNavGraph>
    object Include
}

@Singleton
internal class BookshelfNavGraphTabDisplayRoute : TabDisplayRoute {
    override val routes: List<KClass<*>> =
        listOf(Bookshelf::class, BookshelfFolder::class, BookshelfDelete::class)
}
