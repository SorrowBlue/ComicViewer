package com.sorrowblue.comicviewer.feature.bookshelf.edit.navigation

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.navArgument
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.feature.bookshelf.edit.BookshelfEditRoute

internal const val BookshelfEditRoute = "bookshelf/edit"

fun NavController.navigateToBookshelfEdit(
    bookshelfId: BookshelfId,
    navOptions: NavOptions? = null,
) {
    this.navigate("$BookshelfEditRoute?bookshelf_id=${bookshelfId.value}", navOptions)
}

fun NavController.navigateToBookshelfEdit(
    bookshelfType: BookshelfType,
    navOptions: NavOptions? = null,
) {
    popBackStack()
    this.navigate("$BookshelfEditRoute?type=${bookshelfType.name}", navOptions)
}

fun NavGraphBuilder.bookshelfEditScreen(
    isMobile: Boolean,
    onBackClick: () -> Unit,
    onComplete: () -> Unit,
) {
    if (isMobile) {
        composable(
            route = "$BookshelfEditRoute?bookshelf_id={$BookshelfIdArg}&type={$BookshelfTypeArg}",
            arguments = listOf(
                navArgument(BookshelfIdArg) {
                    type = NavType.IntType
                    defaultValue = BookshelfId.Default
                },
                navArgument(BookshelfTypeArg) {
                    type = NavType.StringType
                    defaultValue = BookshelfType.DEVICE.name
                },
            )
        ) {
            BookshelfEditRoute(
                args = BookshelfEditArgs(it.arguments!!),
                onBackClick = onBackClick,
                onComplete = onComplete
            )
        }
    } else {
        dialog(
            route = "$BookshelfEditRoute?bookshelf_id={$BookshelfIdArg}&type={$BookshelfTypeArg}",
            arguments = listOf(
                navArgument(BookshelfIdArg) {
                    type = NavType.IntType
                    defaultValue = 0
                },
                navArgument(BookshelfTypeArg) {
                    type = NavType.StringType
                    defaultValue = BookshelfType.DEVICE.name
                },
            )
        ) {
            BookshelfEditRoute(
                args = BookshelfEditArgs(it.arguments!!),
                onBackClick = onBackClick,
                onComplete = onComplete
            )
        }
    }
}

internal const val BookshelfIdArg = "bookshelfId"
internal const val BookshelfTypeArg = "bookshelfType"

internal class BookshelfEditArgs(val bookshelfId: BookshelfId, val bookshelfType: BookshelfType) {

    constructor(bundle: Bundle) : this(
        BookshelfId(checkNotNull(bundle.getInt(BookshelfIdArg))),
        BookshelfType.valueOf(checkNotNull(bundle.getString(BookshelfTypeArg)))
    )
}
