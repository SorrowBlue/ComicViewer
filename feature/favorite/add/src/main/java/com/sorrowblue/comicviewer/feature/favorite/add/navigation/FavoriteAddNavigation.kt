package com.sorrowblue.comicviewer.feature.favorite.add.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sorrowblue.comicviewer.domain.model.Base64.decodeFromBase64
import com.sorrowblue.comicviewer.domain.model.Base64.encodeToBase64
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.feature.favorite.add.FavoriteAddRoute

private const val BookshelfIdArg = "bookshelfId"
private const val PathArg = "path"

internal class FavoriteAddArgs(
    val bookshelfId: BookshelfId,
    val path: String,
) {
    constructor(savedStateHandle: SavedStateHandle) : this(
        BookshelfId(checkNotNull(savedStateHandle[BookshelfIdArg])),
        (checkNotNull(savedStateHandle[PathArg]) as String).decodeFromBase64(),
    )
}

private const val FavoriteAddRoute = "favorite/add"

fun NavGraphBuilder.favoriteAddScreen(onBackClick: () -> Unit, onAddClick: () -> Unit) {
    composable(
        route = "$FavoriteAddRoute?bookshelfId={$BookshelfIdArg}&path={$PathArg}",
        arguments = listOf(
            navArgument(BookshelfIdArg) { type = NavType.IntType },
            navArgument(PathArg) { type = NavType.StringType }

        ),
        enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Up,
                animationSpec = tween(700)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Down,
                animationSpec = tween(700)
            )
        }
    ) {
        FavoriteAddRoute(onBackClick = onBackClick, onAddClick = onAddClick)
    }
}

fun NavController.navigateToFavoriteAdd(
    bookshelfId: BookshelfId,
    path: String,
    navOptions: NavOptions? = null,
) {
    navigate(
        "$FavoriteAddRoute?bookshelfId=${bookshelfId.value}&path=${path.encodeToBase64()}",
        navOptions
    )
}
