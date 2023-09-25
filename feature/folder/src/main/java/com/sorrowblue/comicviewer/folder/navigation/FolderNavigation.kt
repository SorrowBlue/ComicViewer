package com.sorrowblue.comicviewer.folder.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sorrowblue.comicviewer.domain.model.Base64.decodeFromBase64
import com.sorrowblue.comicviewer.domain.model.Base64.encodeToBase64
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.folder.FolderRoute

private const val bookshelfIdArg = "bookshelfId"
private const val pathArg = "path"
private const val positionArg = "position"

internal class FolderArgs(
    val bookshelfId: BookshelfId,
    val path: String,
    val position: Int
) {
    constructor(savedStateHandle: SavedStateHandle) : this(
        BookshelfId(checkNotNull(savedStateHandle[bookshelfIdArg])),
        (checkNotNull(savedStateHandle[pathArg]) as String).decodeFromBase64(),
        checkNotNull(savedStateHandle[positionArg])
    )
}

private const val FolderRoute = "folder"

fun folderRoute(prefix: String) =
    "$prefix/$FolderRoute/{$bookshelfIdArg}/{$pathArg}?position={${positionArg}}"

fun NavController.navigateToFolder(
    prefix: String,
    bookshelfId: BookshelfId,
    path: String,
    position: Int = -1,
    navOptions: NavOptions? = null
) {
    navigate(
        "$prefix/$FolderRoute/${bookshelfId.value}/${path.encodeToBase64()}?position=$position",
        navOptions
    )
}

fun NavGraphBuilder.folderScreen(
    prefix: String,
    contentPadding: PaddingValues,
    navigateToSearch: (BookshelfId, String) -> Unit,
    onClickFile: (File, Int) -> Unit,
    onClickLongFile: (File) -> Unit = {},
    onSettingsClick: () -> Unit,
    onBackClick: () -> Unit,
    onRestoreComplete: () -> Unit = {},
) {
    composable(
        route = folderRoute(prefix),
        arguments = listOf(
            navArgument(bookshelfIdArg) { type = NavType.IntType },
            navArgument(pathArg) { type = NavType.StringType },
            navArgument(positionArg) { type = NavType.IntType },
        )
    ) {
        FolderRoute(
            contentPadding = contentPadding,
            onSearchClick = navigateToSearch,
            onSettingsClick = onSettingsClick,
            onBackClick = onBackClick,
            onRestoreComplete = onRestoreComplete,
            onClickFile = onClickFile,
            onClickLongFile = onClickLongFile,
        )
    }
}
