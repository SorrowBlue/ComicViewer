package com.sorrowblue.comicviewer.feature.bookshelf.navgraph

import com.sorrowblue.comicviewer.feature.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.feature.bookshelf.BookshelfFolder
import com.sorrowblue.comicviewer.feature.bookshelf.edit.BookshelfEdit
import com.sorrowblue.comicviewer.feature.bookshelf.info.delete.BookshelfDelete
import com.sorrowblue.comicviewer.feature.bookshelf.selection.BookshelfSelection
import com.sorrowblue.comicviewer.framework.annotation.DestinationInGraph
import com.sorrowblue.comicviewer.framework.annotation.NavGraph
import kotlinx.serialization.Serializable

@Serializable
@NavGraph(startDestination = Bookshelf::class, transition = BookshelfNavGraphTransitions::class)
data object BookshelfNavGraph {

    @DestinationInGraph<Bookshelf>
    @DestinationInGraph<BookshelfDelete>
    @DestinationInGraph<BookshelfEdit>
    @DestinationInGraph<BookshelfFolder>
    @DestinationInGraph<BookshelfSelection>
    object Include
}
