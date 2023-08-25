package com.sorrowblue.comicviewer.feature.search.navigation

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
import com.sorrowblue.comicviewer.feature.search.SearchRoute

const val searchRoute = "search"

fun NavController.navigateToSearch(
    bookshelfId: BookshelfId,
    path: String,
    navOptions: NavOptions? = null
) {
    this.navigate("$searchRoute?bookshelf_id=${bookshelfId.value}&path=${path.encodeToBase64()}", navOptions)
}

fun NavGraphBuilder.searchScreen(onBackClick: () -> Unit) {
    composable(
        route = "$searchRoute?bookshelf_id={$bookshelfIdArg}&path={$pathArg}",
        arguments = listOf(
            navArgument(bookshelfIdArg) {
                type = NavType.IntType
            },
            navArgument(pathArg) {
                type = NavType.StringType
            },
        )
    ) {
        SearchRoute(onBackClick = onBackClick)
    }
}

private const val bookshelfIdArg = "bookshelfId"
private const val pathArg = "path"

internal class SearchArgs(val bookshelfId: BookshelfId, val path: String) {

    val base64Path get() = path.encodeToBase64()

    constructor(savedStateHandle: SavedStateHandle) :
            this(
                BookshelfId(checkNotNull(savedStateHandle[bookshelfIdArg])),
                (checkNotNull(savedStateHandle[pathArg]) as String).decodeFromBase64(),
            )
}
