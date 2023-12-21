package com.sorrowblue.comicviewer.feature.favorite.edit.navigation

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.navArgument
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.feature.favorite.edit.FavoriteEditRoute

private const val FavoriteIdArg = "favoriteId"

internal class FavoriteEditArgs(
    val favoriteId: FavoriteId,
) {
    constructor(bundle: Bundle) : this(FavoriteId(checkNotNull(bundle.getInt(FavoriteIdArg))))
}

fun NavController.navigateToFavoriteEdit(
    favoriteId: FavoriteId,
    navOptions: NavOptions? = null,
) {
    navigate("favorite/${favoriteId.value}/edit", navOptions)
}

fun NavGraphBuilder.favoriteEditScreen(
    isMobile: Boolean,
    onBackClick: () -> Unit,
    onComplete: () -> Unit,
) {
    if (isMobile) {
        composable(
            route = "favorite/{$FavoriteIdArg}/edit",
            arguments = listOf(
                navArgument(FavoriteIdArg) { type = NavType.IntType },
            )
        ) { navBackStackEntry ->
            with(navBackStackEntry) {
                FavoriteEditRoute(
                    onBackClick = onBackClick,
                    onComplete = onComplete
                )
            }
        }
    } else {
        dialog(
            route = "favorite/{$FavoriteIdArg}/edit",
            arguments = listOf(
                navArgument(FavoriteIdArg) { type = NavType.IntType },
            )
        ) { navBackStackEntry ->
            with(navBackStackEntry) {
                FavoriteEditRoute(
                    onBackClick = onBackClick,
                    onComplete = onComplete
                )
            }
        }
    }
}
