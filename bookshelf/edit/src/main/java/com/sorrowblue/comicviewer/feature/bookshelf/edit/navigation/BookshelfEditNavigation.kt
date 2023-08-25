package com.sorrowblue.comicviewer.feature.bookshelf.edit.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.feature.bookshelf.edit.BookshelfEditRoute

const val bookshelfEditRoute = "bookshelf_edit_route"

fun NavController.navigateToBookshelfEdit(
    bookshelfId: BookshelfId,
    navOptions: NavOptions? = null
) {
    this.navigate("$bookshelfEditRoute?bookshelf_id=${bookshelfId.value}", navOptions)
}

fun NavController.navigateToBookshelfEdit(
    bookshelfType: BookshelfType,
    navOptions: NavOptions? = null
) {
    this.navigate("$bookshelfEditRoute?type=${bookshelfType.name}", navOptions)
}

fun NavGraphBuilder.bookshelfEditScreen(
    onBackClick: () -> Unit,
    onComplete: () -> Unit
) {
    composable(
        route = "$bookshelfEditRoute?bookshelf_id={$bookshelfIdArg}&type={$bookshelfTypeArg}",
        arguments = listOf(
            navArgument(bookshelfIdArg) {
                type = NavType.IntType
                defaultValue = 0
            },
            navArgument(bookshelfTypeArg) {
                type = NavType.StringType
                defaultValue = BookshelfType.DEVICE.name
            },
        )
    ) {
        BookshelfEditRoute(
            onBackClick = onBackClick,
            onComplete = onComplete
        )
    }
}

private const val bookshelfIdArg = "bookshelfId"
private const val bookshelfTypeArg = "bookshelfType"

class BookshelfEditArgs(val bookshelfId: BookshelfId, val bookshelfType: BookshelfType) {
    constructor(savedStateHandle: SavedStateHandle) :
            this(
                BookshelfId(checkNotNull(savedStateHandle.get<Int>(bookshelfIdArg))),
                BookshelfType.valueOf(checkNotNull(savedStateHandle.get<String>(bookshelfTypeArg)))
            )
}
