package com.sorrowblue.comicviewer.app

import com.ramcosta.composedestinations.spec.DestinationSpec
import com.ramcosta.composedestinations.spec.NavGraphSpec
import com.sorrowblue.comicviewer.bookshelf.navigation.BookshelfNavGraph
import com.sorrowblue.comicviewer.favorite.navigation.FavoriteNavGraph
import com.sorrowblue.comicviewer.feature.authentication.navigation.AuthenticationNavGraph
import com.sorrowblue.comicviewer.feature.book.destinations.BookScreenDestination
import com.sorrowblue.comicviewer.feature.book.navigation.BookNavGraph
import com.sorrowblue.comicviewer.feature.favorite.add.destinations.FavoriteAddScreenDestination
import com.sorrowblue.comicviewer.feature.library.navigation.LibraryNavGraph
import com.sorrowblue.comicviewer.feature.readlater.navigation.ReadLaterNavGraph
import com.sorrowblue.comicviewer.feature.search.navigation.SearchNavGraph
import com.sorrowblue.comicviewer.feature.settings.navigation.SettingsNavGraph
import com.sorrowblue.comicviewer.feature.tutorial.navigation.TutorialNavGraph

object RootNavGraph : NavGraphSpec {
    override val destinationsByRoute = listOf(
        FavoriteAddScreenDestination,
        BookScreenDestination
    ).associateBy(DestinationSpec<*>::route)
    override val route = "root"
    override val startRoute = BookshelfNavGraph
    override val nestedNavGraphs = listOf(
        BookshelfNavGraph,
        FavoriteNavGraph,
        ReadLaterNavGraph,
        BookNavGraph,
        SearchNavGraph,
        SettingsNavGraph,
        AuthenticationNavGraph,
        TutorialNavGraph,
        LibraryNavGraph,
    )
}

val NavGraphSpec.allNestedNavGraphs: List<NavGraphSpec>
    get() = nestedNavGraphs + nestedNavGraphs.flatMap(NavGraphSpec::allNestedNavGraphs)
