package com.sorrowblue.comicviewer.favorite.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sorrowblue.comicviewer.domain.entity.favorite.FavoriteId
import com.sorrowblue.comicviewer.favorite.edit.FavoriteEditRoute

private const val favoriteIdArg = "favoriteId"

internal class FavoriteEditArgs(
    val favoriteId: FavoriteId
) {
    constructor(savedStateHandle: SavedStateHandle) :
            this(FavoriteId(checkNotNull(savedStateHandle[favoriteIdArg])))
}

internal fun NavGraphBuilder.favoriteEditScreen(
    onBackClick: () -> Unit,
    onComplete: () -> Unit
) {
    composable(
        route = "$FavoriteListRoute/{${favoriteIdArg}}/edit",
        arguments = listOf(
            navArgument(favoriteIdArg) { type = NavType.IntType },
        )
    ) {
        FavoriteEditRoute(onBackClick = onBackClick, onComplete = onComplete)
    }
}

internal fun NavController.navigateToFavoriteEdit(
    favoriteId: FavoriteId,
    navOptions: NavOptions? = null
) {
    navigate(
        "$FavoriteListRoute/${favoriteId.value}/edit",
        navOptions
    )
}
