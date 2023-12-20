package com.sorrowblue.comicviewer.favorite.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.favorite.list.FavoriteListRoute

const val FavoriteListRoute = "favorite"

internal fun NavGraphBuilder.favoriteListScreen(
    onSettingsClick: () -> Unit,
    onFavoriteClick: (FavoriteId) -> Unit,
    contentPadding: PaddingValues,
) {
    composable(FavoriteListRoute) {
        it.FavoriteListRoute(
            contentPadding = contentPadding,
            onSettingsClick = onSettingsClick,
            onFavoriteClick = onFavoriteClick
        )
    }
}
