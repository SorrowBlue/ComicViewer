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

const val ReadLaterRoute = "readlater"
val ReadLaterFolderRoute = folderRoute(ReadLaterRoute)

internal fun NavGraphBuilder.readLaterScreen(
    contentPadding: PaddingValues,
    onFileClick: (File) -> Unit,
    onAddFavoriteClick: (File) -> Unit,
    onOpenFolderClick: (File) -> Unit,
    onSettingsClick: () -> Unit
) {
    composable(route = ReadLaterRoute) {
        ReadLaterRoute(
            contentPadding = contentPadding,
            onFileClick = onFileClick,
            onAddFavoriteClick = onAddFavoriteClick,
            onOpenFolderClick = onOpenFolderClick,
            onSettingsClick = onSettingsClick
        )
    }
}


const val ReadlaterGroupRoute = "readlater_group"

fun NavGraphBuilder.readlaterGroup(
    contentPadding: PaddingValues,
    navController: NavController,
    onBookClick: (BookshelfId, String) -> Unit,
    onSettingsClick: () -> Unit,
    onAddFavoriteClick: (BookshelfId, String) -> Unit,
    navigateToSearch: (BookshelfId, String) -> Unit
) {
    navigation(route = ReadlaterGroupRoute, startDestination = ReadLaterRoute) {
        readLaterScreen(
            contentPadding = contentPadding,
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
            onAddFavoriteClick = { onAddFavoriteClick(it.bookshelfId, it.path) },
            onOpenFolderClick = {
                navController.navigateToFolder(it.bookshelfId, it.path, prefix = ReadLaterRoute)
            },
            onSettingsClick = onSettingsClick
        )
        folderScreen(
            contentPadding = contentPadding,
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
            onAddFavoriteClick = { onAddFavoriteClick(it.bookshelfId, it.path) },
        )
    }
}
