package com.sorrowblue.comicviewer.feature.favorite.add.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.utils.composable
import com.ramcosta.composedestinations.utils.dialogComposable
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.feature.favorite.add.FavoriteAddScreen
import com.sorrowblue.comicviewer.feature.favorite.add.destinations.FavoriteAddScreenDestination
import com.sorrowblue.comicviewer.framework.ui.ComposeValue

class FavoriteAddArgs(
    val bookshelfId: BookshelfId,
    val path: String,
)

context(ComposeValue)
fun NavGraphBuilder.favoriteAddScreen(onBackClick: () -> Unit) {
    if (isCompact) {
        composable(FavoriteAddScreenDestination) {
            FavoriteAddScreen(
                savedStateHandle = navBackStackEntry.savedStateHandle,
                onBackClick = onBackClick,
                contentPadding = contentPadding,
            )
        }
    } else {
        dialogComposable(FavoriteAddScreenDestination) {
            FavoriteAddScreen(
                savedStateHandle = navBackStackEntry.savedStateHandle,
                onBackClick = onBackClick,
                contentPadding = contentPadding,
            )
        }
    }
}

fun NavController.navigateToFavoriteAdd(file: File) {
    navigate(FavoriteAddScreenDestination(file.bookshelfId, file.path))
}
