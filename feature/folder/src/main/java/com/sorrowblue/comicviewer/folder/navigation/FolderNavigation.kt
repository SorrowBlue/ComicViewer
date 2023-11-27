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

private const val BookshelfIdArg = "bookshelfId"
private const val PathArg = "path"
private const val PositionArg = "position"
private const val RestorePathArg = "restorePath"

internal class FolderArgs(
    val bookshelfId: BookshelfId,
    val path: String,
    val position: Int,
    val restorePath: String?,
) {
    constructor(savedStateHandle: SavedStateHandle) : this(
        BookshelfId(checkNotNull(savedStateHandle[BookshelfIdArg])),
        (checkNotNull(savedStateHandle[PathArg]) as String).decodeFromBase64(),
        checkNotNull(savedStateHandle[PositionArg]),
        savedStateHandle.get<String>(RestorePathArg)?.decodeFromBase64()
    )
}

private const val FolderRoute = "folder"

fun folderRoute(prefix: String) =
    "$prefix/$FolderRoute/{$BookshelfIdArg}/{$PathArg}?position={$PositionArg}&$RestorePathArg={$RestorePathArg}"

fun NavController.navigateToFolder(
    prefix: String,
    bookshelfId: BookshelfId,
    path: String,
    position: Int = -1,
    restorePath: String? = null,
    navOptions: NavOptions? = null,
) {
    if (restorePath != null) {
        navigate(
            "$prefix/$FolderRoute/${bookshelfId.value}/${path.encodeToBase64()}?position=$position&$RestorePathArg=${restorePath.encodeToBase64()}",
            navOptions
        )
    } else {
        navigate(
            "$prefix/$FolderRoute/${bookshelfId.value}/${path.encodeToBase64()}?position=$position",
            navOptions
        )
    }
}

fun NavGraphBuilder.folderScreen(
    prefix: String,
    contentPadding: PaddingValues,
    onBackClick: () -> Unit,
    onSearchClick: (BookshelfId, String) -> Unit,
    onSettingsClick: () -> Unit,
    onClickFile: (File) -> Unit,
    onFavoriteClick: (File) -> Unit,
    onRestoreComplete: () -> Unit = {},
) {
    composable(
        route = folderRoute(prefix),
        arguments = listOf(
            navArgument(BookshelfIdArg) { type = NavType.IntType },
            navArgument(PathArg) { type = NavType.StringType },
            navArgument(PositionArg) { type = NavType.IntType },
            navArgument(RestorePathArg) {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            },
        )
    ) {
        FolderRoute(
            contentPadding = contentPadding,
            onSearchClick = onSearchClick,
            onSettingsClick = onSettingsClick,
            onBackClick = onBackClick,
            onRestoreComplete = onRestoreComplete,
            onClickFile = onClickFile,
            onOpenFolderClick = {},
            onFavoriteClick = onFavoriteClick
        )
    }
}
