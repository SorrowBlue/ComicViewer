package com.sorrowblue.comicviewer.favorite.navigation

import androidx.navigation.NavGraphBuilder
import com.ramcosta.composedestinations.utils.composable
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.favorite.destinations.FavoriteListScreenDestination
import com.sorrowblue.comicviewer.favorite.list.FavoriteListScreen
import com.sorrowblue.comicviewer.framework.ui.ComposeValue

context(ComposeValue)
internal fun NavGraphBuilder.favoriteListScreen(
    onSettingsClick: () -> Unit,
    onFavoriteClick: (FavoriteId) -> Unit,
) {
    composable(FavoriteListScreenDestination) {
        FavoriteListScreen(
            savedStateHandle = navBackStackEntry.savedStateHandle,
            contentPadding = contentPadding,
            onSettingsClick = onSettingsClick,
            onFavoriteClick = onFavoriteClick,
        )
    }
}
