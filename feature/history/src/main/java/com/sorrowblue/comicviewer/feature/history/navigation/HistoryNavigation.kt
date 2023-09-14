package com.sorrowblue.comicviewer.feature.history.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.feature.history.HistoryRoute
import com.sorrowblue.comicviewer.folder.navigation.folderRoute
import com.sorrowblue.comicviewer.folder.navigation.folderScreen
import com.sorrowblue.comicviewer.folder.navigation.navigateToFolder

private const val historyRoute = "history"
private const val historyGraphRoute = "${historyRoute}_graph"
val routeInHistoryGraph get() = listOf(historyRoute, folderRoute(historyRoute))

fun NavController.navigateToHistoryGroup() = navigate(historyGraphRoute)

internal fun NavGraphBuilder.historyScreen(
    contentPadding: PaddingValues,
    onFileClick: (File, Int) -> Unit,
    onAddFavoriteClick: (File) -> Unit,
    onOpenFolderClick: (File) -> Unit,
    onSettingsClick: () -> Unit
) {
    composable(route = historyRoute) {
        HistoryRoute(
            contentPadding = contentPadding,
            onFileClick = onFileClick,
            onAddFavoriteClick = onAddFavoriteClick,
            onOpenFolderClick = onOpenFolderClick,
            onSettingsClick = onSettingsClick
        )
    }
}

fun NavGraphBuilder.historyGroup(
    contentPadding: PaddingValues,
    navController: NavController,
    onBookClick: (BookshelfId, String, Int) -> Unit,
    onSettingsClick: () -> Unit,
    onAddFavoriteClick: (BookshelfId, String) -> Unit,
    navigateToSearch: (BookshelfId, String) -> Unit
) {
    navigation(route = historyGraphRoute, startDestination = historyRoute) {
        historyScreen(
            contentPadding = contentPadding,
            onFileClick = { file, position ->
                when (file) {
                    is Book -> onBookClick(file.bookshelfId, file.path, position)
                    is Folder -> navController.navigateToFolder(
                        prefix = historyRoute,
                        bookshelfId = file.bookshelfId,
                        path = file.parent
                    )
                }
            },
            onAddFavoriteClick = { onAddFavoriteClick(it.bookshelfId, it.path) },
            onOpenFolderClick = {
                navController.navigateToFolder(
                    prefix = historyRoute,
                    bookshelfId = it.bookshelfId,
                    path = it.parent,
                )
            },
            onSettingsClick = onSettingsClick
        )
        folderScreen(
            contentPadding = contentPadding,
            prefix = historyRoute,
            navigateToSearch = navigateToSearch,
            onClickFile = { file, position ->
                when (file) {
                    is Book -> onBookClick(file.bookshelfId, file.path, position)
                    is Folder ->
                        navController.navigateToFolder(
                            prefix = historyRoute,
                            file.bookshelfId,
                            file.path,
                        )
                }
            },
            onSettingsClick = onSettingsClick,
            onBackClick = navController::popBackStack,
            onAddFavoriteClick = { onAddFavoriteClick(it.bookshelfId, it.path) },
        )
    }
}
