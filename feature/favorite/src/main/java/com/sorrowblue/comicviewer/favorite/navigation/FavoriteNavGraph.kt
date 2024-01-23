package com.sorrowblue.comicviewer.favorite.navigation

import com.ramcosta.composedestinations.spec.DestinationSpec
import com.sorrowblue.comicviewer.favorite.destinations.FavoriteFolderScreenDestination
import com.sorrowblue.comicviewer.favorite.destinations.FavoriteListScreenDestination
import com.sorrowblue.comicviewer.favorite.destinations.FavoriteScreenDestination
import com.sorrowblue.comicviewer.feature.favorite.edit.destinations.FavoriteEditScreenDestination
import com.sorrowblue.comicviewer.framework.ui.AnimatedNavGraphSpec
import com.sorrowblue.comicviewer.framework.ui.TransitionsConfigure

object FavoriteNavGraph : AnimatedNavGraphSpec {

    override val route = "favorite_graph"

    override val startRoute = FavoriteListScreenDestination

    override val destinationsByRoute: Map<String, DestinationSpec<*>> = listOf(
        FavoriteListScreenDestination,
        FavoriteScreenDestination,
        FavoriteEditScreenDestination,
        FavoriteFolderScreenDestination
    ).associateBy(DestinationSpec<*>::route)

    override val transitions = listOf(
        TransitionsConfigure(
            FavoriteListScreenDestination.route,
            FavoriteScreenDestination.route,
            TransitionsConfigure.Type.SharedAxisX
        ),
        TransitionsConfigure(
            FavoriteScreenDestination.route,
            FavoriteFolderScreenDestination.route,
            TransitionsConfigure.Type.SharedAxisX
        ),
        TransitionsConfigure(
            FavoriteFolderScreenDestination.route,
            FavoriteFolderScreenDestination.route,
            TransitionsConfigure.Type.SharedAxisX
        ),
        TransitionsConfigure(
            FavoriteScreenDestination.route,
            FavoriteEditScreenDestination.route,
            TransitionsConfigure.Type.SharedAxisY
        ),
        TransitionsConfigure(
            route,
            null,
            TransitionsConfigure.Type.ContainerTransform
        )
    )
}

val RouteInFavoriteGraph = listOf(
    FavoriteListScreenDestination.route,
    FavoriteScreenDestination.route,
    FavoriteFolderScreenDestination.route,
)
