package com.sorrowblue.comicviewer.favorite.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.utils.composable
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.favorite.FavoriteScreen
import com.sorrowblue.comicviewer.favorite.destinations.FavoriteScreenDestination
import com.sorrowblue.comicviewer.framework.ui.ComposeValue

class FavoriteArgs(val favoriteId: FavoriteId)

internal fun NavController.navigateToFavorite(favoriteId: FavoriteId) {
    navigate(FavoriteScreenDestination(favoriteId))
}

context(ComposeValue)
internal fun NavGraphBuilder.favoriteScreen(
    onBackClick: () -> Unit,
    onEditClick: (FavoriteId) -> Unit,
    onSettingsClick: () -> Unit,
    onClickFile: (File, FavoriteId) -> Unit,
    onOpenFolderClick: (File) -> Unit,
    onFavoriteClick: (File) -> Unit,
) {
    composable(FavoriteScreenDestination) {
        FavoriteScreen(
            args = navArgs,
            savedStateHandle = navBackStackEntry.savedStateHandle,
            contentPadding = contentPadding,
            onBackClick = onBackClick,
            onEditClick = onEditClick,
            onSettingsClick = onSettingsClick,
            onClickFile = onClickFile,
            onFavoriteClick = onFavoriteClick,
            onOpenFolderClick = onOpenFolderClick,
        )
    }
}
