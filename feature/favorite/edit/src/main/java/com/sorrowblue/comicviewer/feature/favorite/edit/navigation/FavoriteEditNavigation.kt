package com.sorrowblue.comicviewer.feature.favorite.edit.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sorrowblue.comicviewer.domain.entity.favorite.FavoriteId

private const val favoriteIdArg = "favoriteId"

internal class FavoriteEditArgs(
    val favoriteId: FavoriteId
) {
    constructor(savedStateHandle: SavedStateHandle) :
            this(FavoriteId(checkNotNull(savedStateHandle[favoriteIdArg])))
}

fun NavController.navigateToFavoriteEdit(
    favoriteId: FavoriteId,
    navOptions: NavOptions? = null
) {
    navigate("favorite/${favoriteId.value}/edit", navOptions)
}

fun NavGraphBuilder.favoriteEditScreen(
    onBackClick: () -> Unit,
    onComplete: () -> Unit
) {
    composable(
        route = "favorite/{$favoriteIdArg}/edit",
        arguments = listOf(
            navArgument(favoriteIdArg) { type = NavType.IntType },
        )
    ) {
        com.sorrowblue.comicviewer.feature.favorite.edit.FavoriteEditRoute(
            onBackClick = onBackClick,
            onComplete = onComplete
        )
    }
}
