package com.sorrowblue.comicviewer.feature.history.navigation

import com.sorrowblue.comicviewer.feature.history.ClearAllHistory
import com.sorrowblue.comicviewer.feature.history.History
import com.sorrowblue.comicviewer.feature.history.HistoryFolder
import com.sorrowblue.comicviewer.framework.annotation.DestinationInGraph
import com.sorrowblue.comicviewer.framework.annotation.NavGraph
import kotlinx.serialization.Serializable

@Serializable
@NavGraph(startDestination = History::class, transition = HistoryNavGraphTransitions::class)
data object HistoryNavGraph {

    @DestinationInGraph<History>
    @DestinationInGraph<HistoryFolder>
    @DestinationInGraph<ClearAllHistory>
    object Include
}
