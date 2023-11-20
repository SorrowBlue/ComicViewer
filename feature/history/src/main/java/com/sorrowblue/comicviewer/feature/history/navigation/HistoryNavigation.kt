package com.sorrowblue.comicviewer.feature.history.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.history.HistoryRoute
import com.sorrowblue.comicviewer.folder.navigation.folderScreen
import com.sorrowblue.comicviewer.folder.navigation.navigateToFolder

private const val HistoryRoute = "history"
private const val HistoryGraphRoute = "${HistoryRoute}_graph"

fun NavController.navigateToHistoryGroup() = navigate(HistoryGraphRoute)

internal fun NavGraphBuilder.historyScreen(
    contentPadding: PaddingValues,
    onFileClick: (File, Int) -> Unit,
    onSettingsClick: () -> Unit,
) {
    composable(route = HistoryRoute) {
        HistoryRoute(
            contentPadding = contentPadding,
            onFileClick = onFileClick,
            onSettingsClick = onSettingsClick
        )
    }
}

fun NavGraphBuilder.historyGroup(
    contentPadding: PaddingValues,
    navController: NavController,
    navigateToBook: (Book, Int) -> Unit,
    onSettingsClick: () -> Unit,
    navigateToSearch: (BookshelfId, String) -> Unit,
    onFavoriteClick: (File) -> Unit,
) {
    navigation(route = HistoryGraphRoute, startDestination = HistoryRoute) {
        historyScreen(
            contentPadding = contentPadding,
            onFileClick = { file, position ->
                when (file) {
                    is Book -> navigateToBook(file, position)
                    is Folder -> navController.navigateToFolder(
                        prefix = HistoryRoute,
                        bookshelfId = file.bookshelfId,
                        path = file.parent
                    )
                }
            },
            onSettingsClick = onSettingsClick
        )
        folderScreen(
            prefix = HistoryRoute,
            contentPadding = contentPadding,
            onBackClick = navController::popBackStack,
            onSearchClick = navigateToSearch,
            onSettingsClick = onSettingsClick,
            onClickFile = { file, position ->
                when (file) {
                    is Book -> navigateToBook(file, position)
                    is Folder -> navController.navigateToFolder(
                        HistoryRoute,
                        file.bookshelfId,
                        file.path
                    )
                }
            },
            onFavoriteClick = onFavoriteClick
        )
    }
}
