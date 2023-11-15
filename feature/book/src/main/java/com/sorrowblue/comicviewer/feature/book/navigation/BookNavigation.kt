package com.sorrowblue.comicviewer.feature.book.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navOptions
import androidx.navigation.navigation
import com.sorrowblue.comicviewer.domain.model.Base64.decodeFromBase64
import com.sorrowblue.comicviewer.domain.model.Base64.encodeToBase64
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.feature.book.BookRoute

private const val BookGraphRoute = "book_graph"
const val BookRoute = "book"

private const val BookshelfIdArg = "bookshelfId"
private const val FavoriteIdArg = "favoriteId"
private const val PathArg = "path"
private const val PositionArg = "position"
private const val BookRouteBase =
    "$BookRoute/{$BookshelfIdArg}/{$PathArg}?favoriteId={$FavoriteIdArg}&position={$PositionArg}"

fun NavGraphBuilder.bookGraph(
    navController: NavController,
    onBackClick: () -> Unit,
) {
    navigation(route = BookGraphRoute, startDestination = BookRoute) {
        bookScreen(
            onBackClick = onBackClick,
            onNextBookClick = {
                navController.navigateToBook(
                    it.bookshelfId,
                    it.path,
                    -1,
                    navOptions = navOptions {
                        popUpTo(BookRouteBase) { inclusive = true }
                    }
                )
            }
        )
    }
}

private fun NavGraphBuilder.bookScreen(onBackClick: () -> Unit, onNextBookClick: (Book) -> Unit) {
    composable(
        BookRouteBase,
        arguments = listOf(
            navArgument(BookshelfIdArg) { type = NavType.IntType },
            navArgument(PathArg) { type = NavType.StringType },
            navArgument(FavoriteIdArg) {
                type = NavType.IntType
                nullable = false
                defaultValue = -1
            },
            navArgument(PositionArg) {
                type = NavType.IntType
                nullable = false
                defaultValue = -1
            },
        )
    ) {
        BookRoute(onBackClick = onBackClick, onNextBookClick = onNextBookClick)
    }
}

fun NavController.navigateToBook(
    bookshelfId: BookshelfId,
    path: String,
    position: Int = -1,
    navOptions: NavOptions? = null,
) {
    navigate(
        "$BookRoute/${bookshelfId.value}/${path.encodeToBase64()}?position=$position",
        navOptions
    )
}

fun NavController.navigateToBook(
    bookshelfId: BookshelfId,
    path: String,
    favoriteId: FavoriteId = FavoriteId(0),
    navOptions: NavOptions? = null,
) {
    navigate(
        "$BookRoute/${bookshelfId.value}/${path.encodeToBase64()}?favoriteId=${favoriteId.value}",
        navOptions
    )
}

class BookArgs(
    val bookshelfId: BookshelfId,
    val path: String,
    val favoriteId: FavoriteId,
    val position: Int,
) {

    constructor(savedStateHandle: SavedStateHandle) : this(
        BookshelfId(checkNotNull(savedStateHandle[BookshelfIdArg])),
        (checkNotNull(savedStateHandle[PathArg]) as String).decodeFromBase64(),
        FavoriteId(checkNotNull(savedStateHandle[FavoriteIdArg])),
        checkNotNull(savedStateHandle[PositionArg]),
    )
}
