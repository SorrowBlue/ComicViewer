package com.sorrowblue.comicviewer.readlater.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.folder.navigation.folderScreen
import com.sorrowblue.comicviewer.folder.navigation.navigateToFolder
import com.sorrowblue.comicviewer.readlater.ReadLaterRoute

const val ReadLaterRoute = "readlater"

internal fun NavGraphBuilder.readLaterScreen(
    onFileClick: (File) -> Unit,
    onAddFavoriteClick: (File) -> Unit,
    onOpenFolderClick: (File) -> Unit,
    onSettingsClick: () -> Unit
) {
    composable(route = ReadLaterRoute) {
        ReadLaterRoute(
            onFileClick = onFileClick,
            onAddFavoriteClick = onAddFavoriteClick,
            onOpenFolderClick = onOpenFolderClick,
            onSettingsClick = onSettingsClick
        )
    }
}


const val ReadlaterGroupRoute = "readlater_group"

fun NavGraphBuilder.readlaterGroup(
    navController: NavController,
    onBookClick: (BookshelfId, String) -> Unit,
    onSettingsClick: () -> Unit,
    onAddFavoriteClick: (File) -> Unit,
    navigateToSearch: (BookshelfId, String) -> Unit
) {
    navigation(route = ReadlaterGroupRoute, startDestination = ReadLaterRoute) {
        readLaterScreen(
            onFileClick = {
                when (it) {
                    is Book -> onBookClick(it.bookshelfId, it.path)
                    is Folder ->
                        navController.navigateToFolder(
                            it.bookshelfId,
                            it.path,
                            prefix = ReadLaterRoute
                        )
                }
            },
            onAddFavoriteClick = onAddFavoriteClick,
            onOpenFolderClick = {
                navController.navigateToFolder(it.bookshelfId, it.path, prefix = ReadLaterRoute)
            },
            onSettingsClick = onSettingsClick
        )
        folderScreen(
            prefix = ReadLaterRoute,
            navigateToSearch = navigateToSearch,
            onClickFile = {
                when (it) {
                    is Book -> onBookClick(it.bookshelfId, it.path)
                    is Folder ->
                        navController.navigateToFolder(
                            it.bookshelfId,
                            it.path,
                            prefix = ReadLaterRoute
                        )
                }
            },
            onSettingsClick = onSettingsClick,
            onBackClick = navController::popBackStack,
            onAddFavoriteClick = onAddFavoriteClick
        )
    }
}
