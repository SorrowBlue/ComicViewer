package com.sorrowblue.comicviewer.feature.book.navigation

import android.os.Bundle
import androidx.compose.foundation.layout.PaddingValues
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
private const val NameArg = "name"

private const val BookRouteBase =
    "$BookRoute/{$BookshelfIdArg}/{$PathArg}?$FavoriteIdArg={$FavoriteIdArg}&$PositionArg={$PositionArg}&$NameArg={$NameArg}"

fun NavGraphBuilder.bookGraph(
    navController: NavController,
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit,
    contentPadding: PaddingValues,
) {
    navigation(route = BookGraphRoute, startDestination = BookRoute) {
        bookScreen(
            onBackClick = onBackClick,
            onSettingsClick = onSettingsClick,
            onNextBookClick = {
                navController.navigateToBook(
                    it,
                    -1,
                    navOptions = navOptions { popUpTo(BookRouteBase) { inclusive = true } }
                )
            },
            contentPadding = contentPadding
        )
    }
}

private fun NavGraphBuilder.bookScreen(
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onNextBookClick: (Book) -> Unit,
    contentPadding: PaddingValues,
) {
    composable(
        BookRouteBase,
        arguments = listOf(
            navArgument(BookshelfIdArg) { type = NavType.IntType },
            navArgument(PathArg) { type = NavType.StringType },
            navArgument(NameArg) {
                type = NavType.StringType
                nullable = false
                defaultValue = ""
            },
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
        BookRoute(
            args = BookArgs(it.arguments!!),
            onBackClick = onBackClick,
            onSettingsClick = onSettingsClick,
            onNextBookClick = onNextBookClick,
            contentPadding = contentPadding,
        )
    }
}

fun NavController.navigateToBook(
    book: Book,
    position: Int = -1,
    navOptions: NavOptions? = null,
) {
    navigate(
        "$BookRoute/${book.bookshelfId.value}/${book.path.encodeToBase64()}?$PositionArg=$position&$NameArg=${book.name}",
        navOptions
    )
}

fun NavController.navigateToBook(
    bookshelfId: BookshelfId,
    path: String,
    favoriteId: FavoriteId,
    name: String,
    navOptions: NavOptions? = null,
) {
    navigate(
        "$BookRoute/${bookshelfId.value}/${path.encodeToBase64()}?$NameArg=$name&$FavoriteIdArg=${favoriteId.value}",
        navOptions
    )
}

class BookArgs(
    val bookshelfId: BookshelfId,
    val path: String,
    val favoriteId: FavoriteId,
    val position: Int,
    val name: String,
) {

    constructor(savedStateHandle: SavedStateHandle) : this(
        BookshelfId(checkNotNull(savedStateHandle[BookshelfIdArg])),
        (checkNotNull(savedStateHandle[PathArg]) as String).decodeFromBase64(),
        FavoriteId(checkNotNull(savedStateHandle[FavoriteIdArg])),
        checkNotNull(savedStateHandle[PositionArg]),
        savedStateHandle.get<String>(NameArg).orEmpty(),
    )

    constructor(bundle: Bundle) : this(
        BookshelfId(bundle.getInt(BookshelfIdArg)),
        checkNotNull(bundle.getString(PathArg)).decodeFromBase64(),
        FavoriteId(bundle.getInt(FavoriteIdArg)),
        bundle.getInt(PositionArg),
        bundle.getString(NameArg).orEmpty(),
    )
}
