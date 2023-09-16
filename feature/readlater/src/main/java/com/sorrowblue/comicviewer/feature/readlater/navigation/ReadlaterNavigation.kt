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
    onBookClick: (BookshelfId, String, Int) -> Unit,
    onFileLongClick: (File) -> Unit,
    onSettingsClick: () -> Unit,
    navigateToSearch: (BookshelfId, String) -> Unit
) {
    navigation(route = readlaterGraphRoute, startDestination = readLaterRoute) {
        readLaterScreen(
            contentPadding = contentPadding,
            onFileClick = { file, position ->
                when (file) {
                    is Book -> onBookClick(file.bookshelfId, file.path, position)
                    is Folder ->
                        navController.navigateToFolder(
                            prefix = readLaterRoute,
                            file.bookshelfId,
                            file.path
                        )
                }
            },
            onFileLongClick = onFileLongClick,
            onSettingsClick = onSettingsClick,
        )
        folderScreen(
            contentPadding = contentPadding,
            prefix = readLaterRoute,
            navigateToSearch = navigateToSearch,
            onClickFile = { file, position ->
                when (file) {
                    is Book -> onBookClick(file.bookshelfId, file.path, position)
                    is Folder ->
                        navController.navigateToFolder(
                            prefix = readLaterRoute,
                            file.bookshelfId,
                            file.path
                        )
                }
            },
            onSettingsClick = onSettingsClick,
            onBackClick = navController::popBackStack,
        )
    }
}

private fun NavGraphBuilder.readLaterScreen(
    contentPadding: PaddingValues,
    onFileClick: (File, Int) -> Unit,
    onFileLongClick: (File) -> Unit,
    onSettingsClick: () -> Unit
) {
    composable(route = readLaterRoute) {
        ReadLaterRoute(
            onFileClick = onFileClick,
            onFileLongClick = onFileLongClick,
            onSettingsClick = onSettingsClick,
            contentPadding = contentPadding,
        )
    }
}
