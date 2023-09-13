package com.sorrowblue.comicviewer.feature.readlater.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.feature.readlater.ReadLaterRoute
import com.sorrowblue.comicviewer.folder.navigation.folderRoute
import com.sorrowblue.comicviewer.folder.navigation.folderScreen
import com.sorrowblue.comicviewer.folder.navigation.navigateToFolder

private const val readLaterRoute = "readlater"
val routeInReadlaterGraph get() = listOf(readLaterRoute, folderRoute(readLaterRoute))
const val readlaterGraphRoute = "readlater_graph"

fun NavGraphBuilder.readlaterGroup(
    contentPadding: PaddingValues,
    navController: NavController,
    onBookClick: (BookshelfId, String) -> Unit,
    onSettingsClick: () -> Unit,
    onAddFavoriteClick: (BookshelfId, String) -> Unit,
    navigateToSearch: (BookshelfId, String) -> Unit
) {
    navigation(route = readlaterGraphRoute, startDestination = readLaterRoute) {
        readLaterScreen(
            contentPadding = contentPadding,
            onFileClick = {
                when (it) {
                    is Book -> onBookClick(it.bookshelfId, it.path)
                    is Folder ->
                        navController.navigateToFolder(
                            prefix = readLaterRoute,
                            it.bookshelfId,
                            it.path
                        )
                }
            },
            onAddFavoriteClick = { onAddFavoriteClick(it.bookshelfId, it.path) },
            onOpenFolderClick = {
                navController.navigateToFolder(prefix = readLaterRoute, it.bookshelfId, it.path)
            },
            onSettingsClick = onSettingsClick
        )
        folderScreen(
            contentPadding = contentPadding,
            prefix = readLaterRoute,
            navigateToSearch = navigateToSearch,
            onClickFile = {
                when (it) {
                    is Book -> onBookClick(it.bookshelfId, it.path)
                    is Folder ->
                        navController.navigateToFolder(
                            prefix = readLaterRoute,
                            it.bookshelfId,
                            it.path
                        )
                }
            },
            onSettingsClick = onSettingsClick,
            onBackClick = navController::popBackStack,
            onAddFavoriteClick = { onAddFavoriteClick(it.bookshelfId, it.path) },
        )
    }
}

private fun NavGraphBuilder.readLaterScreen(
    contentPadding: PaddingValues,
    onFileClick: (File) -> Unit,
    onAddFavoriteClick: (File) -> Unit,
    onOpenFolderClick: (File) -> Unit,
    onSettingsClick: () -> Unit
) {
    composable(route = readLaterRoute) {
        ReadLaterRoute(
            contentPadding = contentPadding,
            onFileClick = onFileClick,
            onAddFavoriteClick = onAddFavoriteClick,
            onOpenFolderClick = onOpenFolderClick,
            onSettingsClick = onSettingsClick
        )
    }
}
