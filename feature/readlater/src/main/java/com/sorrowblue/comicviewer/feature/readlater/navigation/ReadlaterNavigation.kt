package com.sorrowblue.comicviewer.feature.readlater.navigation

import androidx.navigation.NavGraphBuilder
import com.ramcosta.composedestinations.utils.composable
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.readlater.ReadLaterScreen
import com.sorrowblue.comicviewer.feature.readlater.destinations.ReadLaterScreenDestination
import com.sorrowblue.comicviewer.folder.navigation.folderRoute
import com.sorrowblue.comicviewer.folder.navigation.folderScreen
import com.sorrowblue.comicviewer.folder.navigation.navigateToFolder
import com.sorrowblue.comicviewer.framework.ui.ComposeTransition
import com.sorrowblue.comicviewer.framework.ui.ComposeValue
import com.sorrowblue.comicviewer.framework.ui.animatedNavigation

val RouteInReadlaterGraph =
    listOf(ReadLaterScreenDestination.route, folderRoute(ReadLaterScreenDestination.route))
const val ReadlaterGraphRoute = "readlater_graph"

context(ComposeValue)
fun NavGraphBuilder.readlaterGroup(
    navigateToBook: (Book) -> Unit,
    onSettingsClick: () -> Unit,
    navigateToSearch: (BookshelfId, String) -> Unit,
    onFavoriteClick: (File) -> Unit,
) {
    animatedNavigation(
        startDestination = ReadLaterScreenDestination.route,
        route = ReadlaterGraphRoute,
        transitions = listOf(
            ComposeTransition(
                ReadLaterScreenDestination.route,
                folderRoute(ReadLaterScreenDestination.route),
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
                            prefix = ReadLaterScreenDestination.route,
                            file.bookshelfId,
                            file.path
                        )
                }
            },
            onFavoriteClick = onFavoriteClick,
            onOpenFolderClick = {
                navController.navigateToFolder(
                    ReadLaterScreenDestination.route,
                    it.bookshelfId,
                    it.parent
                )
            },
            onSettingsClick = onSettingsClick,
        )
        folderScreen(
            prefix = ReadLaterScreenDestination.route,
            onSearchClick = navigateToSearch,
            onSettingsClick = onSettingsClick,
            onClickFile = { file ->
                when (file) {
                    is Book -> navigateToBook(file)
                    is Folder -> navController.navigateToFolder(
                        ReadLaterScreenDestination.route,
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
    composable(ReadLaterScreenDestination) {
        ReadLaterScreen(
            savedStateHandle = navBackStackEntry.savedStateHandle,
            contentPadding = contentPadding,
            onSettingsClick = onSettingsClick,
            onFileClick = onFileClick,
            onFavoriteClick = onFavoriteClick,
            onOpenFolderClick = onOpenFolderClick,
        )
    }
}
