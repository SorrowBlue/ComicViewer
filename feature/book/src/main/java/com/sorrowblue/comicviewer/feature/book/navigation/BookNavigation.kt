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

private const val bookGraphRoute = "book_graph"
const val bookRoute = "book"

private const val bookshelfIdArg = "bookshelfId"
private const val favoriteIdArg = "favoriteId"
private const val pathArg = "path"
private const val positionArg = "position"
private const val BOOK_ROUTE_BASE =
    "$bookRoute/{$bookshelfIdArg}/{$pathArg}?favoriteId={$favoriteIdArg}&position={${positionArg}}"

fun NavGraphBuilder.bookGraph(
    navController: NavController,
    onBackClick: () -> Unit
) {
    navigation(route = bookGraphRoute, startDestination = bookRoute) {
        bookScreen(
            onBackClick = onBackClick,
            onNextBookClick = {
                navController.navigateToBook(
                    it.bookshelfId,
                    it.path,
                    -1,
                    navOptions = navOptions {
                        popUpTo(BOOK_ROUTE_BASE) { inclusive = true }
                    }
                )
            }
        )
    }
}

private fun NavGraphBuilder.bookScreen(onBackClick: () -> Unit, onNextBookClick: (Book) -> Unit) {
    composable(
        BOOK_ROUTE_BASE,
        arguments = listOf(
            navArgument(bookshelfIdArg) { type = NavType.IntType },
            navArgument(pathArg) { type = NavType.StringType },
            navArgument(favoriteIdArg) {
                type = NavType.IntType
                nullable = false
                defaultValue = -1
            },
            navArgument(positionArg) {
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
    navOptions: NavOptions? = null
) {
    navigate(
        "$bookRoute/${bookshelfId.value}/${path.encodeToBase64()}?position=$position",
        navOptions
    )
}


fun NavController.navigateToBook(
    bookshelfId: BookshelfId,
    path: String,
    favoriteId: FavoriteId = FavoriteId(0),
    navOptions: NavOptions? = null
) {
    navigate(
        "$bookRoute/${bookshelfId.value}/${path.encodeToBase64()}?favoriteId=${favoriteId.value}",
        navOptions
    )
}

class BookArgs(
    val bookshelfId: BookshelfId,
    val path: String,
    val favoriteId: FavoriteId,
    val position: Int
) {

    constructor(savedStateHandle: SavedStateHandle) :
            this(
                BookshelfId(checkNotNull(savedStateHandle[bookshelfIdArg])),
                (checkNotNull(savedStateHandle[pathArg]) as String).decodeFromBase64(),
                FavoriteId(checkNotNull(savedStateHandle[favoriteIdArg])),
                checkNotNull(savedStateHandle[positionArg]),
            )
}
