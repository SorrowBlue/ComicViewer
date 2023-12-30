package com.sorrowblue.comicviewer.bookshelf.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sorrowblue.comicviewer.bookshelf.BookshelfRoute
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.folder.navigation.folderRoute
import com.sorrowblue.comicviewer.folder.navigation.navigateToFolder
import com.sorrowblue.comicviewer.framework.ui.ComposeValue

const val BookshelfRoute = "bookshelf"
const val BookshelfGraphRoute = "bookshelf_graph"
val routeInBookshelfGraph get() = listOf(BookshelfRoute, folderRoute(BookshelfRoute))

context(ComposeValue)
internal fun NavGraphBuilder.bookshelfScreen(
    onSettingsClick: () -> Unit,
    onFabClick: () -> Unit,
    onBookshelfClick: (BookshelfId, String) -> Unit,
    onEditClick: (BookshelfId) -> Unit,
) {
    composable(BookshelfRoute) { navBackStackEntry ->
        with(navBackStackEntry) {
            BookshelfRoute(
                contentPadding = contentPadding,
                onSettingsClick = onSettingsClick,
                onFabClick = onFabClick,
                onBookshelfClick = onBookshelfClick,
                onEditClick = onEditClick,
            )
        }
    }
}

fun NavController.navigateToBookshelfFolder(id: BookshelfId, path: String) {
    navigateToFolder(prefix = BookshelfRoute, bookshelfId = id, path = path)
}

fun NavController.navigateToBookshelfFolder(id: BookshelfId, path: String, restorePath: String) {
    navigateToFolder(
        prefix = BookshelfRoute,
        bookshelfId = id,
        path = path,
        restorePath = restorePath
    )
}
