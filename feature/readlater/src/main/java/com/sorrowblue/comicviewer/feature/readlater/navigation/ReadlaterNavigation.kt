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
    navigateToBook: (Book) -> Unit,
    onSettingsClick: () -> Unit,
    navigateToSearch: (BookshelfId, String) -> Unit,
    onFavoriteClick: (File) -> Unit,
) {
    navigation(route = ReadlaterGraphRoute, startDestination = ReadLaterRoute) {
        readLaterScreen(
            contentPadding = contentPadding,
            onFileClick = { file ->
                when (file) {
                    is Book -> navigateToBook(file)
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
            prefix = ReadLaterRoute,
            contentPadding = contentPadding,
            onSearchClick = navigateToSearch,
            onSettingsClick = onSettingsClick,
            onClickFile = { file ->
                when (file) {
                    is Book -> navigateToBook(file)
                    is Folder -> navController.navigateToFolder(
                        ReadLaterRoute,
                        file.bookshelfId,
                        file.path
                    )
                }
            },
            onBackClick = navController::popBackStack,
            onFavoriteClick = onFavoriteClick
        )
    }
}

private fun NavGraphBuilder.readLaterScreen(
    contentPadding: PaddingValues,
    onFileClick: (File) -> Unit,
    onSettingsClick: () -> Unit,
) {
    composable(route = ReadLaterRoute) {
        ReadLaterRoute(
            onFileClick = onFileClick,
            onSettingsClick = onSettingsClick,
            contentPadding = contentPadding,
        )
    }
}
