package com.sorrowblue.comicviewer.feature.bookshelf.edit.navigation

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.feature.bookshelf.edit.devicestorage.DeviceStorageEditScreen2

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
    this.navigate("$BookshelfEditRoute?type=${bookshelfType.name}", navOptions)
}

fun NavGraphBuilder.bookshelfEditScreen(
    onBackClick: () -> Unit,
    onComplete: () -> Unit,
) {
    composable(
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
        DeviceStorageEditScreen2(
            onBackClick = onBackClick
        )
//        val args = BookshelfEditArgs(it.arguments!!)
//        BookshelfEditRoute(
//            args = args,
//            onBackClick = onBackClick,
//            onComplete = onComplete
//        )
    }
}

internal const val BookshelfIdArg = "bookshelfId"
internal const val BookshelfTypeArg = "bookshelfType"

class BookshelfEditArgs(val bookshelfId: BookshelfId, val bookshelfType: BookshelfType) {
    constructor(savedStateHandle: SavedStateHandle) :
            this(
                BookshelfId(checkNotNull(savedStateHandle.get<Int>(BookshelfIdArg))),
                BookshelfType.valueOf(checkNotNull(savedStateHandle.get<String>(BookshelfTypeArg)))
            )

    constructor(bundle: Bundle) :
            this(
                BookshelfId(checkNotNull(bundle.getInt(BookshelfIdArg))),
                BookshelfType.valueOf(checkNotNull(bundle.getString(BookshelfTypeArg)))
            )
}
