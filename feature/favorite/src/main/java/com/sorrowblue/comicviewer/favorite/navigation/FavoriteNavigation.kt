package com.sorrowblue.comicviewer.favorite.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sorrowblue.comicviewer.domain.entity.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.favorite.FavoriteRoute

private const val favoriteIdArg = "favoriteId"

internal class FavoriteArgs(
    val favoriteId: FavoriteId,
) {
    constructor(savedStateHandle: SavedStateHandle) :
            this(FavoriteId(checkNotNull(savedStateHandle[favoriteIdArg])))
}

internal const val favoriteRoute = "$FavoriteListRoute/{$favoriteIdArg}"

internal fun NavController.navigateToFavorite(
    favoriteId: FavoriteId,
    navOptions: NavOptions? = null
) {
    navigate("$FavoriteListRoute/${favoriteId.value}", navOptions)
}

internal fun NavGraphBuilder.favoriteScreen(
    onBackClick: () -> Unit,
    onEditClick: (FavoriteId) -> Unit,
    onSettingsClick: () -> Unit,
    onClickFile: (File) -> Unit
) {
    composable(
        route = favoriteRoute,
        arguments = listOf(
            navArgument(favoriteIdArg) { type = NavType.IntType }
        )
    ) {
        FavoriteRoute(
            onBackClick = onBackClick,
            onEditClick = onEditClick,
            onSettingsClick = onSettingsClick,
            onClickFile = onClickFile
        )
    }
}
