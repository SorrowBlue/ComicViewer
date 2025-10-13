package com.sorrowblue.comicviewer.feature.bookshelf.navgraph

import com.sorrowblue.cmpdestinations.animation.NavTransitions
import com.sorrowblue.cmpdestinations.annotation.NavGraph
import com.sorrowblue.comicviewer.feature.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.feature.bookshelf.BookshelfFolder
import com.sorrowblue.comicviewer.feature.bookshelf.info.delete.BookshelfDelete
import com.sorrowblue.comicviewer.framework.ui.navigation.TabDisplayRoute
import jakarta.inject.Singleton
import kotlin.reflect.KClass
import kotlinx.serialization.Serializable

@Serializable
@NavGraph(
    startDestination = Bookshelf::class,
    destinations = [
        Bookshelf::class,
        BookshelfDelete::class,
        BookshelfFolder::class,
    ],
    transitions = NavTransitions.ApplyParent::class
)
data object BookshelfNavGraph

@Singleton
internal class BookshelfNavGraphTabDisplayRoute : TabDisplayRoute {
    override val routes: List<KClass<*>> =
        listOf(Bookshelf::class, BookshelfFolder::class, BookshelfDelete::class)
}
