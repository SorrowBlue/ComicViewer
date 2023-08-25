package com.sorrowblue.comicviewer.favorite.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.sorrowblue.comicviewer.favorite.FavoriteListRoute
import com.sorrowblue.comicviewer.framework.compose.FabVisibleState

const val FavoriteRoute = "favorite"

private fun NavGraphBuilder.favoriteScreen(
    fabState: FabVisibleState,
    onSettingsClick: () -> Unit,
) {
    composable(FavoriteRoute) {
        FavoriteListRoute(
            onSettingsClick = onSettingsClick,
            fabState = fabState
        )
    }
}

const val FavoriteGroupRoute = "${FavoriteRoute}_group"

fun NavGraphBuilder.favoriteGroup(
    navController: NavController,
    fabState: FabVisibleState,
    onSettingsClick: () -> Unit,
) {
    navigation(route = FavoriteGroupRoute, startDestination = FavoriteRoute) {
        favoriteScreen(fabState, onSettingsClick)
    }
}
