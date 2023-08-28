package com.sorrowblue.comicviewer.feature.readlater.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.folder.navigation.folderRoute
import com.sorrowblue.comicviewer.folder.navigation.folderScreen
import com.sorrowblue.comicviewer.folder.navigation.navigateToFolder
import com.sorrowblue.comicviewer.feature.readlater.ReadLaterRoute

const val ReadLaterRoute = "readlater"
val ShowNavigationBarReadLaterNavGraph = listOf(ReadLaterRoute, folderRoute(ReadLaterRoute))

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
    onAddFavoriteClick: (BookshelfId, String) -> Unit,
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
            onAddFavoriteClick = { onAddFavoriteClick(it.bookshelfId, it.path) },
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
            onAddFavoriteClick = { onAddFavoriteClick(it.bookshelfId, it.path) }
        )
    }
}
