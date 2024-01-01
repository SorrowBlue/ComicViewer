package com.sorrowblue.comicviewer.feature.favorite.edit.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.utils.composable
import com.ramcosta.composedestinations.utils.dialogComposable
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.feature.favorite.edit.FavoriteEditScreen
import com.sorrowblue.comicviewer.feature.favorite.edit.destinations.FavoriteEditScreenDestination
import com.sorrowblue.comicviewer.framework.ui.ComposeValue

class FavoriteEditArgs(val favoriteId: FavoriteId)

fun NavController.navigateToFavoriteEdit(favoriteId: FavoriteId) {
    navigate(FavoriteEditScreenDestination(favoriteId))
}

context(ComposeValue)
fun NavGraphBuilder.favoriteEditScreen(
    onBackClick: () -> Unit,
    onComplete: () -> Unit,
) {
    if (isCompact) {
        composable(FavoriteEditScreenDestination) {
            FavoriteEditScreen(
                args = navArgs,
                savedStateHandle = navBackStackEntry.savedStateHandle,
                onBackClick = onBackClick,
                onComplete = onComplete,
            )
        }
    } else {
        dialogComposable(FavoriteEditScreenDestination) {
            FavoriteEditScreen(
                args = navArgs,
                savedStateHandle = navBackStackEntry.savedStateHandle,
                onBackClick = onBackClick,
                onComplete = onComplete,
            )
        }
    }
}
