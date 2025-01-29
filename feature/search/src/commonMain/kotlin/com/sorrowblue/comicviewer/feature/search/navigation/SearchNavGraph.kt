package com.sorrowblue.comicviewer.feature.search.navigation

import com.sorrowblue.comicviewer.feature.search.Search
import com.sorrowblue.comicviewer.feature.search.SearchFolder
import com.sorrowblue.comicviewer.framework.annotation.DestinationInGraph
import com.sorrowblue.comicviewer.framework.annotation.NavGraph
import kotlinx.serialization.Serializable

@NavGraph(startDestination = Search::class, transition = SearchNavGraphTransitions::class)
@Serializable
data object SearchNavGraph {
    @DestinationInGraph<Search>
    @DestinationInGraph<SearchFolder>
    object Include
}
