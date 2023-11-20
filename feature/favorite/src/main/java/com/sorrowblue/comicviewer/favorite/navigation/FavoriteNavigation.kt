package com.sorrowblue.comicviewer.favorite.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.favorite.FavoriteRoute

private const val FavoriteIdArg = "favoriteId"

internal class FavoriteArgs(
    val favoriteId: FavoriteId,
) {
    constructor(savedStateHandle: SavedStateHandle) : this(FavoriteId(checkNotNull(savedStateHandle[FavoriteIdArg])))
}

const val FavoriteRoute = "$FavoriteListRoute/{$FavoriteIdArg}"

internal fun NavController.navigateToFavorite(
    favoriteId: FavoriteId,
    navOptions: NavOptions? = null,
) {
    navigate("$FavoriteListRoute/${favoriteId.value}", navOptions)
}

internal fun NavGraphBuilder.favoriteScreen(
    onBackClick: () -> Unit,
    onEditClick: (FavoriteId) -> Unit,
    onSettingsClick: () -> Unit,
    onClickFile: (File, FavoriteId, Int) -> Unit,
    onClickLongFile: (File) -> Unit,
) {
    composable(
        route = FavoriteRoute,
        arguments = listOf(
            navArgument(FavoriteIdArg) { type = NavType.IntType }
        )
    ) {
        FavoriteRoute(
            onBackClick = onBackClick,
            onEditClick = onEditClick,
            onSettingsClick = onSettingsClick,
            onClickFile = onClickFile,
            onClickLongFile = onClickLongFile,
        )
    }
}
