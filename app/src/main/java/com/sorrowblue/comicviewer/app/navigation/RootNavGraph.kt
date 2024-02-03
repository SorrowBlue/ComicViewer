package com.sorrowblue.comicviewer.app.navigation

import com.ramcosta.composedestinations.spec.DestinationSpec
import com.ramcosta.composedestinations.spec.NavGraphSpec
import com.sorrowblue.comicviewer.bookshelf.navigation.BookshelfNavGraph
import com.sorrowblue.comicviewer.favorite.navigation.FavoriteNavGraph
import com.sorrowblue.comicviewer.feature.book.destinations.BookScreenDestination
import com.sorrowblue.comicviewer.feature.book.navigation.BookNavGraph
import com.sorrowblue.comicviewer.feature.favorite.add.destinations.FavoriteAddScreenDestination
import com.sorrowblue.comicviewer.feature.library.navigation.LibraryNavGraph
import com.sorrowblue.comicviewer.feature.readlater.navigation.ReadLaterNavGraph
import com.sorrowblue.comicviewer.feature.search.navigation.SearchNavGraph
import com.sorrowblue.comicviewer.feature.settings.navigation.SettingsNavGraph
import com.sorrowblue.comicviewer.feature.tutorial.navigation.TutorialNavGraph

internal object RootNavGraph : NavGraphSpec {

    override val route = "root"

    override val startRoute = BookshelfNavGraph

    override val destinationsByRoute = listOf(
        FavoriteAddScreenDestination,
        BookScreenDestination
    ).associateBy(DestinationSpec<*>::route)

    override val nestedNavGraphs = listOf(
        BookshelfNavGraph,
        FavoriteNavGraph,
        ReadLaterNavGraph,
        BookNavGraph,
        SearchNavGraph,
        SettingsNavGraph,
        TutorialNavGraph,
        LibraryNavGraph,
    )
}
