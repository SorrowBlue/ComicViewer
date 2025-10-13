package com.sorrowblue.comicviewer.feature.history.navigation

import com.sorrowblue.cmpdestinations.animation.NavTransitions
import com.sorrowblue.cmpdestinations.annotation.NavGraph
import com.sorrowblue.comicviewer.feature.history.ClearAllHistory
import com.sorrowblue.comicviewer.feature.history.History
import com.sorrowblue.comicviewer.feature.history.HistoryFolder
import com.sorrowblue.comicviewer.framework.ui.navigation.TabDisplayRoute
import kotlin.reflect.KClass
import kotlinx.serialization.Serializable
import jakarta.inject.Singleton

@Serializable
@NavGraph(
    startDestination = History::class,
    destinations = [
        History::class,
        HistoryFolder::class,
        ClearAllHistory::class,
    ],
    transitions = NavTransitions.ApplyParent::class
)
data object HistoryNavGraph

@Singleton
internal class HistoryNavGraphTabDisplayRoute : TabDisplayRoute {
    override val routes: List<KClass<*>> =
        listOf(History::class, HistoryFolder::class, ClearAllHistory::class)
}
