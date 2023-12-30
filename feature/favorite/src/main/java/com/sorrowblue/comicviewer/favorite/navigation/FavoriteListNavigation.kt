package com.sorrowblue.comicviewer.favorite.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.favorite.list.FavoriteListRoute
import com.sorrowblue.comicviewer.framework.ui.ComposeValue

const val FavoriteListRoute = "favorite"

context(ComposeValue)
internal fun NavGraphBuilder.favoriteListScreen(
    onSettingsClick: () -> Unit,
    onFavoriteClick: (FavoriteId) -> Unit,
) {
    composable(FavoriteListRoute) { navBackStackEntry ->
        with(navBackStackEntry) {
            FavoriteListRoute(
                contentPadding = contentPadding,
                onSettingsClick = onSettingsClick,
                onFavoriteClick = onFavoriteClick
            )
        }
    }
}
