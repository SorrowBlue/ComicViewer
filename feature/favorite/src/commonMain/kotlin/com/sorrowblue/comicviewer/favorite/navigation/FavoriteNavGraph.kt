package com.sorrowblue.comicviewer.favorite.navigation

import com.sorrowblue.cmpdestinations.annotation.DestinationInGraph
import com.sorrowblue.cmpdestinations.annotation.NavGraph
import com.sorrowblue.comicviewer.favorite.Favorite
import com.sorrowblue.comicviewer.favorite.FavoriteFolder
import com.sorrowblue.comicviewer.favorite.list.FavoriteList
import com.sorrowblue.comicviewer.feature.favorite.edit.FavoriteEdit
import kotlinx.serialization.Serializable

@NavGraph(startDestination = FavoriteList::class, transitions = FavoriteNavGraphTransitions::class)
@Serializable
data object FavoriteNavGraph {

    @DestinationInGraph<Favorite>
    @DestinationInGraph<FavoriteFolder>
    @DestinationInGraph<FavoriteEdit>
    @DestinationInGraph<FavoriteList>
    object Include
}
