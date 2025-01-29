package com.sorrowblue.comicviewer.feature.book.navigation

import com.sorrowblue.comicviewer.feature.book.Book
import com.sorrowblue.comicviewer.feature.book.menu.BookMenu
import com.sorrowblue.comicviewer.framework.annotation.DestinationInGraph
import com.sorrowblue.comicviewer.framework.annotation.NavGraph
import kotlinx.serialization.Serializable

@NavGraph(startDestination = Book::class, transition = BookNavGraphTransitions::class)
@Serializable
data object BookNavGraph {

    @DestinationInGraph<Book>
    @DestinationInGraph<BookMenu>
    object Include
}
