package com.sorrowblue.comicviewer.book.compose

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sorrowblue.comicviewer.book.BookScreen
import com.sorrowblue.comicviewer.book.rememberBookScreenState
import com.sorrowblue.comicviewer.domain.Base64.decodeFromBase64
import com.sorrowblue.comicviewer.domain.Base64.encodeToBase64
import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfId

const val BookRoute = "book"

private const val bookshelfIdArg = "bookshelfId"
private const val pathArg = "path"

fun NavGraphBuilder.bookScreen(navController: NavController) {
    composable(
        "$BookRoute/{$bookshelfIdArg}/{$pathArg}",
        arguments = listOf(
            navArgument(bookshelfIdArg) { type = NavType.IntType },
            navArgument(pathArg) { type = NavType.StringType }
        )
    ) {
        val bookScreenState =
            rememberBookScreenState(navController = navController, composableBackStackEntry = it)
        BookScreen(bookScreenState)
    }
}


fun NavController.navigateToBook(args: BookArgs, navOptions: NavOptions? = null) {
    navigate("$BookRoute/${args.bookshelfId.value}/${args.base64Path}", navOptions)
}

fun NavController.navigateToBook(bookshelfId: BookshelfId,path: String, navOptions: NavOptions? = null) {
    navigate("$BookRoute/${bookshelfId.value}/${path.encodeToBase64()}", navOptions)
}

class BookArgs(val bookshelfId: BookshelfId, val path: String) {
    val base64Path get() = path.encodeToBase64()

    constructor(savedStateHandle: SavedStateHandle) :
            this(
                BookshelfId(checkNotNull(savedStateHandle[bookshelfIdArg])),
                (checkNotNull(savedStateHandle[pathArg]) as String).decodeFromBase64()
            )
}
