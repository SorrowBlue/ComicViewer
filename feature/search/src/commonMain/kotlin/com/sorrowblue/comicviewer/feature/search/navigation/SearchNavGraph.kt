package com.sorrowblue.comicviewer.feature.search.navigation

import com.sorrowblue.cmpdestinations.animation.NavTransitions
import com.sorrowblue.cmpdestinations.annotation.NavGraph
import com.sorrowblue.comicviewer.feature.search.Search
import com.sorrowblue.comicviewer.feature.search.SearchFolder
import kotlinx.serialization.Serializable

@NavGraph(
    startDestination = Search::class,
    destinations = [Search::class, SearchFolder::class],
    transitions = NavTransitions.ApplyParent::class
)
@Serializable
data object SearchNavGraph
