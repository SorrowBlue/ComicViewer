package com.sorrowblue.comicviewer.favorite.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sorrowblue.comicviewer.domain.entity.favorite.FavoriteId
import com.sorrowblue.comicviewer.favorite.list.FavoriteListRoute

internal const val FavoriteListRoute = "favorite"

internal fun NavGraphBuilder.favoriteListScreen(
    onSettingsClick: () -> Unit,
    onAddClick: () -> Unit,
    onFavoriteClick: (FavoriteId) -> Unit,
    contentPadding: PaddingValues,
) {
    composable(FavoriteListRoute) {
        FavoriteListRoute(
            contentPadding = contentPadding,
            onSettingsClick = onSettingsClick,
            onAddClick = onAddClick,
            onFavoriteClick = onFavoriteClick,
        )
    }
}
