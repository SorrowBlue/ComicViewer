package com.sorrowblue.comicviewer.feature.search.navigation

import com.sorrowblue.cmpdestinations.annotation.NavGraph
import com.sorrowblue.comicviewer.feature.search.Search
import com.sorrowblue.comicviewer.feature.search.SearchFolder
import kotlinx.serialization.Serializable

@NavGraph(
    startDestination = Search::class,
    transitions = SearchNavGraphTransitions::class,
    destinations = [Search::class, SearchFolder::class]
)
@Serializable
data object SearchNavGraph
