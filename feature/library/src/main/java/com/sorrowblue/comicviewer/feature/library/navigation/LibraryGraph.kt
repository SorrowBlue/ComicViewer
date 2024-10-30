package com.sorrowblue.comicviewer.feature.library.navigation

import com.ramcosta.composedestinations.annotation.ExternalModuleGraph
import com.ramcosta.composedestinations.annotation.ExternalNavGraph
import com.ramcosta.composedestinations.annotation.NavGraph
import com.ramcosta.composedestinations.spec.BaseRoute
import com.ramcosta.composedestinations.spec.DestinationSpec
import com.ramcosta.composedestinations.spec.DestinationStyle
import com.ramcosta.composedestinations.spec.DirectionNavGraphSpec
import com.ramcosta.composedestinations.spec.NavGraphSpec
import com.ramcosta.composedestinations.spec.TypedRoute
import com.sorrowblue.comicviewer.feature.history.navgraphs.HistoryNavGraph
import com.sorrowblue.comicviewer.feature.library.destinations.LibraryScreenDestination
import com.sorrowblue.comicviewer.feature.library.serviceloader.BoxNavGraph
import com.sorrowblue.comicviewer.feature.library.serviceloader.DropBoxNavGraph
import com.sorrowblue.comicviewer.feature.library.serviceloader.GoogleDriveNavGraph
import com.sorrowblue.comicviewer.feature.library.serviceloader.OneDriveNavGraph

@NavGraph<ExternalModuleGraph>
internal annotation class LibraryGraph {

    @ExternalNavGraph<HistoryNavGraph>(defaultTransitions = LibraryGraphTransitions::class)
    companion object Includes
}

data object LibraryNavGraph : BaseRoute(), DirectionNavGraphSpec {

    override val startRoute: TypedRoute<Unit> = LibraryScreenDestination

    override val destinations: List<DestinationSpec>
        get() = listOf(LibraryScreenDestination)

    override val defaultTransitions: DestinationStyle.Animated = LibraryGraphTransitions

    override val route: String = "library"

    override val nestedNavGraphs: List<NavGraphSpec>
        get() = listOfNotNull(
            HistoryNavGraph,
            BoxNavGraph()?.navGraph,
            GoogleDriveNavGraph()?.navGraph,
            OneDriveNavGraph()?.navGraph,
            DropBoxNavGraph()?.navGraph,
        )
}
