package com.sorrowblue.comicviewer.feature.search.navigation

import com.sorrowblue.cmpdestinations.annotation.DestinationInGraph
import com.sorrowblue.cmpdestinations.annotation.NavGraph
import com.sorrowblue.comicviewer.feature.search.Search
import com.sorrowblue.comicviewer.feature.search.SearchFolder
import kotlinx.serialization.Serializable

@NavGraph(startDestination = Search::class, transitions = SearchNavGraphTransitions::class)
@Serializable
data object SearchNavGraph {
    @DestinationInGraph<Search>
    @DestinationInGraph<SearchFolder>
    object Include
}
