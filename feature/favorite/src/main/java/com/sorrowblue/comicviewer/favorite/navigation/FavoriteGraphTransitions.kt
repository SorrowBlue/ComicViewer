package com.sorrowblue.comicviewer.favorite.navigation

import com.ramcosta.composedestinations.spec.DestinationSpec
import com.sorrowblue.comicviewer.feature.favorite.NavGraphs
import com.sorrowblue.comicviewer.feature.favorite.destinations.FavoriteFolderScreenDestination
import com.sorrowblue.comicviewer.feature.favorite.destinations.FavoriteListScreenDestination
import com.sorrowblue.comicviewer.feature.favorite.destinations.FavoriteScreenDestination
import com.sorrowblue.comicviewer.feature.favorite.edit.destinations.FavoriteEditScreenDestination
import com.sorrowblue.comicviewer.framework.ui.DestinationTransitions
import com.sorrowblue.comicviewer.framework.ui.TransitionsConfigure

internal object FavoriteGraphTransitions : DestinationTransitions() {

    override val directionToDisplayNavigation: List<DestinationSpec> = listOf(
        FavoriteListScreenDestination,
        FavoriteScreenDestination,
        FavoriteFolderScreenDestination
    )

    override val transitions = listOf(
        TransitionsConfigure(
            FavoriteListScreenDestination,
            FavoriteScreenDestination,
            TransitionsConfigure.Type.SharedAxisX
        ),
        TransitionsConfigure(
            FavoriteScreenDestination,
            FavoriteFolderScreenDestination,
            TransitionsConfigure.Type.SharedAxisX
        ),
        TransitionsConfigure(
            FavoriteFolderScreenDestination,
            FavoriteFolderScreenDestination,
            TransitionsConfigure.Type.SharedAxisX
        ),
        TransitionsConfigure(
            FavoriteScreenDestination,
            FavoriteEditScreenDestination,
            TransitionsConfigure.Type.SharedAxisY
        ),
        TransitionsConfigure(
            NavGraphs.favorite,
            null,
            TransitionsConfigure.Type.ContainerTransform
        )
    )
}
