package com.sorrowblue.comicviewer.app.navigation

import androidx.navigation.NavType
import com.sorrowblue.cmpdestinations.Destination
import com.sorrowblue.cmpdestinations.GraphNavigation
import com.sorrowblue.cmpdestinations.ScreenDestination
import com.sorrowblue.cmpdestinations.animation.NavTransitions
import com.sorrowblue.cmpdestinations.annotation.NavGraph
import com.sorrowblue.comicviewer.feature.book.navigation.BookNavGraph
import com.sorrowblue.comicviewer.feature.bookshelf.navgraph.BookshelfNavGraph
import com.sorrowblue.comicviewer.feature.collection.navigation.CollectionNavGraph
import com.sorrowblue.comicviewer.feature.history.navigation.HistoryNavGraph
import com.sorrowblue.comicviewer.feature.readlater.navigation.ReadLaterNavGraph
import com.sorrowblue.comicviewer.feature.search.navigation.SearchNavGraph
import com.sorrowblue.comicviewer.feature.settings.navigation.SettingsNavGraph
import com.sorrowblue.comicviewer.folder.SortTypeSelect
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlinx.serialization.Serializable

@NavGraph(
    startDestination = BookshelfNavGraph::class,
    destinations = [SortTypeSelect::class],
    nestedGraphs = [
        BookshelfNavGraph::class,
        ReadLaterNavGraph::class,
        CollectionNavGraph::class,
        SearchNavGraph::class,
        SettingsNavGraph::class,
        HistoryNavGraph::class,
        BookNavGraph::class,
    ]
)
@Serializable
internal expect object ComicViewerAppNavGraph : GraphNavigation {
    override val destinations: Array<Destination>
    override val nestedGraphs: Array<GraphNavigation>
    override val route: KClass<*>
    override val startDestination: KClass<*>
    override val transitions: NavTransitions
    override val typeMap: Map<KType, NavType<*>>
}
