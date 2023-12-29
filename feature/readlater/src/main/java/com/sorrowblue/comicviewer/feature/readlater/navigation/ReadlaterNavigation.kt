package com.sorrowblue.comicviewer.feature.readlater.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.readlater.ReadLaterRoute
import com.sorrowblue.comicviewer.folder.navigation.folderRoute
import com.sorrowblue.comicviewer.folder.navigation.folderScreen
import com.sorrowblue.comicviewer.folder.navigation.navigateToFolder
import com.sorrowblue.comicviewer.framework.ui.ComposeTransition
import com.sorrowblue.comicviewer.framework.ui.ComposeValue
import com.sorrowblue.comicviewer.framework.ui.animatedNavigation

private const val ReadLaterRoute = "readlater"
val RouteInReadlaterGraph = listOf(ReadLaterRoute, folderRoute(ReadLaterRoute))
const val ReadlaterGraphRoute = "readlater_graph"

context(ComposeValue)
fun NavGraphBuilder.readlaterGroup(
    navigateToBook: (Book) -> Unit,
    onSettingsClick: () -> Unit,
    navigateToSearch: (BookshelfId, String) -> Unit,
    onFavoriteClick: (File) -> Unit,
) {
    animatedNavigation(
        startDestination = ReadLaterRoute,
        route = ReadlaterGraphRoute,
        transitions = listOf(
            ComposeTransition(
                ReadLaterRoute,
                folderRoute(ReadLaterRoute),
                ComposeTransition.Type.SharedAxisX
            ),
            ComposeTransition(
                ReadlaterGraphRoute,
                null,
                ComposeTransition.Type.ContainerTransform
            )
        )
    ) {
        readLaterScreen(
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
            onFavoriteClick = onFavoriteClick,
            onOpenFolderClick = {
                navController.navigateToFolder(ReadLaterRoute, it.bookshelfId, it.parent)
            },
            onSettingsClick = onSettingsClick,
        )
        folderScreen(
            prefix = ReadLaterRoute,
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

context(ComposeValue)
private fun NavGraphBuilder.readLaterScreen(
    onFileClick: (File) -> Unit,
    onFavoriteClick: (File) -> Unit,
    onOpenFolderClick: (File) -> Unit,
    onSettingsClick: () -> Unit,
) {
    composable(route = ReadLaterRoute) { navBackStackEntry ->
        with(navBackStackEntry) {
            ReadLaterRoute(
                onFileClick = onFileClick,
                onSettingsClick = onSettingsClick,
                contentPadding = contentPadding,
                onFavoriteClick = onFavoriteClick,
                onOpenFolderClick = onOpenFolderClick,
            )
        }
    }
}
