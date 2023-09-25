package com.sorrowblue.comicviewer.file.info.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import com.sorrowblue.comicviewer.domain.model.Base64.decodeFromBase64
import com.sorrowblue.comicviewer.domain.model.Base64.encodeToBase64
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.favorite.add.navigation.favoriteAddScreen
import com.sorrowblue.comicviewer.feature.favorite.add.navigation.navigateToFavoriteAdd
import com.sorrowblue.comicviewer.file.info.FileInfoRoute
import com.sorrowblue.comicviewer.folder.navigation.folderScreen
import com.sorrowblue.comicviewer.folder.navigation.navigateToFolder

const val fileInfoGraph = "file_graph"

private const val fileInfoRoute = "file"

private val bookshelfIdArg = "bookshelfId"
private val pathArg = "path"
private val showOpenFolderArg = "showOpenFolder"

internal class FileInfoArgs(
    val bookshelfId: BookshelfId,
    val path: String,
    val isVisibleOpenFolder: Boolean,
) {
    constructor(savedStateHandle: SavedStateHandle) : this(
        BookshelfId(checkNotNull(savedStateHandle[bookshelfIdArg])),
        (checkNotNull<String>(savedStateHandle[pathArg])).decodeFromBase64(),
        checkNotNull(savedStateHandle[showOpenFolderArg]),
    )
}


fun NavController.navigateToFileInfo(
    id: BookshelfId,
    path: String,
    showOpenFolder: Boolean = true,
) {
    this.navigate("$fileInfoRoute/${id.value}/${path.encodeToBase64()}?showOpenFolder=$showOpenFolder")
}

@OptIn(ExperimentalMaterialNavigationApi::class)
internal fun NavGraphBuilder.fileInfoScreen(
    onDismissRequest: () -> Unit,
    onAddFavoriteClick: (File) -> Unit,
    onOpenFolderClick: (File) -> Unit,
) {
    bottomSheet(
        route = "$fileInfoRoute/{$bookshelfIdArg}/{$pathArg}?showOpenFolder={$showOpenFolderArg}",
        arguments = listOf(
            navArgument(bookshelfIdArg) { type = NavType.IntType },
            navArgument(pathArg) { type = NavType.StringType },
            navArgument(showOpenFolderArg) {
                type = NavType.BoolType
                defaultValue = true
            },
        )
    ) {
        FileInfoRoute(
            onDismissRequest = onDismissRequest,
            onAddFavoriteClick = onAddFavoriteClick,
            onOpenFolderClick = onOpenFolderClick
        )
    }
}

fun NavGraphBuilder.fileInfoGraph(
    navController: NavController,
    contentPadding: PaddingValues,
    onClickBook: (BookshelfId, String, Int) -> Unit,
    navigateToSearch: (BookshelfId, String) -> Unit,
    onSettingsClick: () -> Unit,
    onAddClick: () -> Unit,
) {
    navigation(route = fileInfoGraph, startDestination = fileInfoRoute) {
        fileInfoScreen(
            onDismissRequest = navController::popBackStack,
            onAddFavoriteClick = {
                navController.navigateToFavoriteAdd(it.bookshelfId, it.path)
            },
            onOpenFolderClick = {
                navController.navigateToFolder(fileInfoRoute, it.bookshelfId, it.parent)
            }
        )
        favoriteAddScreen(onBackClick = navController::popBackStack, onAddClick = onAddClick)
        folderScreen(
            fileInfoRoute,
            contentPadding = contentPadding,
            navigateToSearch = navigateToSearch,
            onClickFile = { it, pos ->
                when (it) {
                    is Book -> onClickBook(it.bookshelfId, it.path, pos)
                    is Folder -> navController.navigateToFolder(
                        fileInfoRoute,
                        it.bookshelfId,
                        it.path
                    )
                }
            },
            onClickLongFile = {
                navController.navigateToFileInfo(it.bookshelfId, it.path)
            },
            onSettingsClick = onSettingsClick,
            onBackClick = navController::popBackStack,
            onRestoreComplete = {
                throw RuntimeException("ナビゲーション履歴の復元は実施されない")
            }
        )
    }
}
