package com.sorrowblue.comicviewer.favorite.navigation

import com.sorrowblue.comicviewer.favorite.Favorite
import com.sorrowblue.comicviewer.favorite.FavoriteFolder
import com.sorrowblue.comicviewer.favorite.list.FavoriteList
import com.sorrowblue.comicviewer.feature.favorite.edit.FavoriteEdit
import com.sorrowblue.comicviewer.framework.annotation.DestinationInGraph
import com.sorrowblue.comicviewer.framework.annotation.NavGraph
import kotlinx.serialization.Serializable

@NavGraph(startDestination = FavoriteList::class, transition = FavoriteNavGraphTransitions::class)
@Serializable
data object FavoriteNavGraph {

    @DestinationInGraph<Favorite>
    @DestinationInGraph<FavoriteFolder>
    @DestinationInGraph<FavoriteEdit>
    @DestinationInGraph<FavoriteList>
    object Include
}
