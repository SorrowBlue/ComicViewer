package com.sorrowblue.comicviewer.favorite.navigation

import com.ramcosta.composedestinations.annotation.ExternalDestination
import com.ramcosta.composedestinations.annotation.ExternalModuleGraph
import com.ramcosta.composedestinations.annotation.NavGraph
import com.sorrowblue.comicviewer.feature.favorite.edit.destinations.FavoriteEditScreenDestination

@NavGraph<ExternalModuleGraph>
internal annotation class FavoriteGraph {

    @ExternalDestination<FavoriteEditScreenDestination>(style = FavoriteGraphTransitions::class)
    companion object Includes
}
