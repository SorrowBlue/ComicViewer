package com.sorrowblue.comicviewer.favorite.navigation

import android.os.Bundle
import androidx.compose.foundation.layout.PaddingValues
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
    constructor(bundle: Bundle) : this(FavoriteId(checkNotNull(bundle.getInt(FavoriteIdArg))))
}

const val FavoriteRoute = "$FavoriteListRoute/{$FavoriteIdArg}"

internal fun NavController.navigateToFavorite(
    favoriteId: FavoriteId,
    navOptions: NavOptions? = null,
) {
    navigate("$FavoriteListRoute/${favoriteId.value}", navOptions)
}

internal fun NavGraphBuilder.favoriteScreen(
    contentPadding: PaddingValues,
    onBackClick: () -> Unit,
    onEditClick: (FavoriteId) -> Unit,
    onSettingsClick: () -> Unit,
    onClickFile: (File, FavoriteId) -> Unit,
    onOpenFolderClick: (File) -> Unit,
    onFavoriteClick: (File) -> Unit,
) {
    composable(
        route = FavoriteRoute,
        arguments = listOf(navArgument(FavoriteIdArg) { type = NavType.IntType })
    ) {
        it.FavoriteRoute(
            contentPadding = contentPadding,
            onBackClick = onBackClick,
            onEditClick = onEditClick,
            onSettingsClick = onSettingsClick,
            onClickFile = onClickFile,
            onOpenFolderClick = onOpenFolderClick,
            onFavoriteClick = onFavoriteClick
        )
    }
}
