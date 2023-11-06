package com.sorrowblue.comicviewer.feature.favorite.edit.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId

private const val FavoriteIdArg = "favoriteId"

internal class FavoriteEditArgs(
    val favoriteId: FavoriteId,
) {
    constructor(savedStateHandle: SavedStateHandle) : this(FavoriteId(checkNotNull(savedStateHandle[FavoriteIdArg])))
}

fun NavController.navigateToFavoriteEdit(
    favoriteId: FavoriteId,
    navOptions: NavOptions? = null,
) {
    navigate("favorite/${favoriteId.value}/edit", navOptions)
}

fun NavGraphBuilder.favoriteEditScreen(
    onBackClick: () -> Unit,
    onComplete: () -> Unit,
) {
    composable(
        route = "favorite/{$FavoriteIdArg}/edit",
        arguments = listOf(
            navArgument(FavoriteIdArg) { type = NavType.IntType },
        )
    ) {
        com.sorrowblue.comicviewer.feature.favorite.edit.FavoriteEditRoute(
            onBackClick = onBackClick,
            onComplete = onComplete
        )
    }
}
