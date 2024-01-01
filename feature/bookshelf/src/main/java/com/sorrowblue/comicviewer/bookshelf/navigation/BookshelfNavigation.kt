package com.sorrowblue.comicviewer.bookshelf.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.ramcosta.composedestinations.utils.composable
import com.sorrowblue.comicviewer.bookshelf.BookshelfScreen
import com.sorrowblue.comicviewer.bookshelf.destinations.BookshelfScreenDestination
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.folder.navigation.navigateToFolder
import com.sorrowblue.comicviewer.framework.ui.ComposeValue

context(ComposeValue)
internal fun NavGraphBuilder.bookshelfScreen(
    onSettingsClick: () -> Unit,
    onFabClick: () -> Unit,
    onBookshelfClick: (BookshelfId, String) -> Unit,
    onEditClick: (BookshelfId) -> Unit,
) {
    composable(BookshelfScreenDestination) {
        BookshelfScreen(
            savedStateHandle = navBackStackEntry.savedStateHandle,
            contentPadding = contentPadding,
            onSettingsClick = onSettingsClick,
            onFabClick = onFabClick,
            onBookshelfClick = onBookshelfClick,
            onEditClick = onEditClick,
        )
    }
}

fun NavController.navigateToBookshelfFolder(id: BookshelfId, path: String) {
    navigateToFolder(prefix = BookshelfScreenDestination.baseRoute, bookshelfId = id, path = path)
}

fun NavController.navigateToBookshelfFolder(id: BookshelfId, path: String, restorePath: String) {
    navigateToFolder(
        prefix = BookshelfScreenDestination.baseRoute,
        bookshelfId = id,
        path = path,
        restorePath = restorePath
    )
}
