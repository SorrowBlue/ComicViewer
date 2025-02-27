package com.sorrowblue.comicviewer.feature.history.navigation

import com.sorrowblue.cmpdestinations.annotation.DestinationInGraph
import com.sorrowblue.cmpdestinations.annotation.NavGraph
import com.sorrowblue.comicviewer.feature.history.ClearAllHistory
import com.sorrowblue.comicviewer.feature.history.History
import com.sorrowblue.comicviewer.feature.history.HistoryFolder
import kotlinx.serialization.Serializable

@Serializable
@NavGraph(startDestination = History::class, transitions = HistoryNavGraphTransitions::class)
data object HistoryNavGraph {

    @DestinationInGraph<History>
    @DestinationInGraph<HistoryFolder>
    @DestinationInGraph<ClearAllHistory>
    object Include
}
