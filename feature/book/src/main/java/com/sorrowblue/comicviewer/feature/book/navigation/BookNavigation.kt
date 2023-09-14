package com.sorrowblue.comicviewer.feature.book.navigation

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
import com.sorrowblue.comicviewer.domain.entity.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.feature.book.BookRoute

const val BookRoute = "book"

private const val bookshelfIdArg = "bookshelfId"
private const val favoriteIdArg = "favoriteId"
private const val pathArg = "path"
private const val positionArg = "position"

fun NavGraphBuilder.bookScreen(onBackClick: () -> Unit, onNextBookClick: (Book) -> Unit) {
    composable(
        "$BookRoute/{$bookshelfIdArg}/{$pathArg}?favoriteId={$favoriteIdArg}&position={${positionArg}}",
        arguments = listOf(
            navArgument(bookshelfIdArg) { type = NavType.IntType },
            navArgument(pathArg) { type = NavType.StringType },
            navArgument(favoriteIdArg) { type = NavType.IntType },
            navArgument(positionArg) { type = NavType.IntType },
        )
    ) {
        BookRoute(onBackClick = onBackClick,onNextBookClick = onNextBookClick)
    }
}


fun NavController.navigateToBook(
    bookshelfId: BookshelfId,
    path: String,
    favoriteId: FavoriteId = FavoriteId(0),
    position: Int = -1,
    navOptions: NavOptions? = null
) {
    navigate(
        "$BookRoute/${bookshelfId.value}/${path.encodeToBase64()}?favoriteId=${favoriteId.value}&position=$position",
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
