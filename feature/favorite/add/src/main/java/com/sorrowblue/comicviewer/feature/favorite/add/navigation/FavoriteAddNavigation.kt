package com.sorrowblue.comicviewer.feature.favorite.add.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sorrowblue.comicviewer.domain.Base64.decodeFromBase64
import com.sorrowblue.comicviewer.domain.Base64.encodeToBase64
import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfId

private const val bookshelfIdArg = "bookshelfId"
private const val pathArg = "path"

internal class FavoriteAddArgs(
    val bookshelfId: BookshelfId,
    val path: String,
) {
    constructor(savedStateHandle: SavedStateHandle) : this(
        BookshelfId(checkNotNull(savedStateHandle[bookshelfIdArg])),
        (checkNotNull(savedStateHandle[pathArg]) as String).decodeFromBase64(),
    )
}

private const val FavoriteAddRoute = "favorite/add"

fun NavGraphBuilder.favoriteAddScreen(onAddClick: () -> Unit) {
    composable(
        route = "$FavoriteAddRoute?bookshelfId={$bookshelfIdArg}&path={$pathArg}",
        arguments = listOf(
            navArgument(bookshelfIdArg) { type = NavType.IntType },
            navArgument(pathArg) { type = NavType.StringType }

        )
    ) {
        com.sorrowblue.comicviewer.feature.favorite.add.FavoriteAddRoute(onAddClick = onAddClick)
    }
}

fun NavController.navigateToFavoriteAdd(
    bookshelfId: BookshelfId,
    path: String,
    navOptions: NavOptions? = null
) {
    navigate(
        "$FavoriteAddRoute?bookshelfId=${bookshelfId.value}&path=${path.encodeToBase64()}",
        navOptions
    )
}
