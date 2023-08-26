package com.sorrowblue.comicviewer.favorite.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sorrowblue.comicviewer.domain.entity.favorite.FavoriteId
import com.sorrowblue.comicviewer.favorite.list.FavoriteListRoute
import com.sorrowblue.comicviewer.framework.compose.FabVisibleState

internal const val FavoriteListRoute = "favorite"

internal fun NavGraphBuilder.favoriteListScreen(
    fabState: FabVisibleState,
    onSettingsClick: () -> Unit,
    onFavoriteClick: (FavoriteId) -> Unit,
) {
    composable(FavoriteListRoute) {
        FavoriteListRoute(
            onSettingsClick = onSettingsClick,
            onFavoriteClick = onFavoriteClick,
            fabState = fabState,
        )
    }
}
