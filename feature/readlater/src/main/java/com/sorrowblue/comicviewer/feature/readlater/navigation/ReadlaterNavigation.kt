package com.sorrowblue.comicviewer.feature.readlater.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.readlater.ReadLaterRoute
import com.sorrowblue.comicviewer.folder.navigation.folderRoute
import com.sorrowblue.comicviewer.folder.navigation.folderScreen
import com.sorrowblue.comicviewer.folder.navigation.navigateToFolder

private const val ReadLaterRoute = "readlater"
val RouteInReadlaterGraph = listOf(ReadLaterRoute, folderRoute(ReadLaterRoute))
const val ReadlaterGraphRoute = "readlater_graph"

fun NavGraphBuilder.readlaterGroup(
    contentPadding: PaddingValues,
    navController: NavController,
    onBookClick: (BookshelfId, String, Int) -> Unit,
    onSettingsClick: () -> Unit,
    navigateToSearch: (BookshelfId, String) -> Unit,
) {
    navigation(route = ReadlaterGraphRoute, startDestination = ReadLaterRoute) {
        readLaterScreen(
            contentPadding = contentPadding,
            onFileClick = { file, position ->
                when (file) {
                    is Book -> onBookClick(file.bookshelfId, file.path, position)
                    is Folder ->
                        navController.navigateToFolder(
                            prefix = ReadLaterRoute,
                            file.bookshelfId,
                            file.path
                        )
                }
            },
            onSettingsClick = onSettingsClick,
        )
        folderScreen(
            contentPadding = contentPadding,
            prefix = ReadLaterRoute,
            navigateToSearch = navigateToSearch,
            onClickFile = { file, position ->
                when (file) {
                    is Book -> onBookClick(file.bookshelfId, file.path, position)
                    is Folder ->
                        navController.navigateToFolder(
                            prefix = ReadLaterRoute,
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
    onSettingsClick: () -> Unit
) {
    composable(route = ReadLaterRoute) {
        ReadLaterRoute(
            onFileClick = onFileClick,
            onSettingsClick = onSettingsClick,
            contentPadding = contentPadding,
        )
    }
}
