package com.sorrowblue.comicviewer.feature.book.navigation

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navOptions
import com.sorrowblue.comicviewer.domain.model.Base64.decodeFromBase64
import com.sorrowblue.comicviewer.domain.model.Base64.encodeToBase64
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.feature.book.BookRoute
import com.sorrowblue.comicviewer.framework.ui.ComposeTransition
import com.sorrowblue.comicviewer.framework.ui.ComposeValue
import com.sorrowblue.comicviewer.framework.ui.animatedComposable

private const val BookRoute = "book"

private const val BookshelfIdArg = "bookshelfId"
private const val FavoriteIdArg = "favoriteId"
private const val PathArg = "path"
private const val NameArg = "name"

private const val BookRouteBase =
    "$BookRoute/{$BookshelfIdArg}/{$PathArg}?$NameArg={$NameArg}&$FavoriteIdArg={$FavoriteIdArg}"

context(ComposeValue)
fun NavGraphBuilder.bookScreen(
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    animatedComposable(
        route = BookRouteBase,
        transitions = listOf(
            ComposeTransition(
                BookRouteBase,
                null,
                ComposeTransition.Type.ContainerTransform
            )
        ),
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
            }
        )
    ) {
        BookRoute(
            args = BookArgs(it.arguments!!),
            onBackClick = onBackClick,
            onSettingsClick = onSettingsClick,
            onNextBookClick = { book, favoriteId ->
                navController.navigateToBook(
                    book = book,
                    favoriteId = favoriteId,
                    navOptions = navOptions { popUpTo(BookRouteBase) { inclusive = true } }
                )
            },
            contentPadding = contentPadding,
        )
    }
}

fun NavController.navigateToBook(
    book: Book,
    favoriteId: FavoriteId = FavoriteId(-1),
    navOptions: NavOptions? = null,
) {
    navigate(
        "$BookRoute/${book.bookshelfId.value}/${book.path.encodeToBase64()}?$NameArg=${book.name}&$FavoriteIdArg=${favoriteId.value}",
        navOptions
    )
}

class BookArgs(
    val bookshelfId: BookshelfId,
    val path: String,
    val name: String,
    val favoriteId: FavoriteId = FavoriteId(-1),
) {

    constructor(bundle: Bundle) : this(
        BookshelfId(bundle.getInt(BookshelfIdArg)),
        checkNotNull(bundle.getString(PathArg)).decodeFromBase64(),
        bundle.getString(NameArg).orEmpty(),
        FavoriteId(bundle.getInt(FavoriteIdArg))
    )
}
