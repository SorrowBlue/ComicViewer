package com.sorrowblue.comicviewer.file.info.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import com.sorrowblue.comicviewer.domain.Base64.decodeFromBase64
import com.sorrowblue.comicviewer.domain.Base64.encodeToBase64
import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.feature.favorite.add.navigation.favoriteAddScreen
import com.sorrowblue.comicviewer.feature.favorite.add.navigation.navigateToFavoriteAdd
import com.sorrowblue.comicviewer.file.info.FileInfoRoute

const val fileInfoGraph = "file_graph"

private const val fileInfoRoute = "file"

private val bookshelfIdArg = "bookshelfId"
private val pathArg = "path"

internal class FileInfoArgs(
    val bookshelfId: BookshelfId,
    val path: String,
) {
    constructor(savedStateHandle: SavedStateHandle) : this(
        BookshelfId(checkNotNull(savedStateHandle[bookshelfIdArg])),
        (checkNotNull<String>(savedStateHandle[pathArg])).decodeFromBase64(),
    )
}


fun NavController.navigateToFileInfo(id: BookshelfId, path: String) {
    this.navigate("$fileInfoRoute/${id.value}/${path.encodeToBase64()}")
}

@OptIn(ExperimentalMaterialNavigationApi::class)
internal fun NavGraphBuilder.fileInfoScreen(
    onDismissRequest: () -> Unit,
    onAddFavoriteClick: (File) -> Unit,
    onOpenFolderClick: (File) -> Unit
) {
    bottomSheet(
        route = "$fileInfoRoute/{$bookshelfIdArg}/{$pathArg}",
        arguments = listOf(
            navArgument(bookshelfIdArg) { type = NavType.IntType },
            navArgument(pathArg) { type = NavType.StringType }
        )
    ) {
        FileInfoRoute(
            onDismissRequest = onDismissRequest,
            onAddFavoriteClick = onAddFavoriteClick,
            onOpenFolderClick = onOpenFolderClick
        )
    }
}

fun NavGraphBuilder.fileInfoGraph(navController: NavController, onOpenFolderClick: (File) -> Unit) {
    navigation(route = fileInfoGraph, startDestination = fileInfoRoute) {
        fileInfoScreen(
            onDismissRequest = navController::popBackStack,
            onAddFavoriteClick = {
                navController.navigateToFavoriteAdd(it.bookshelfId, it.path)
            },
            onOpenFolderClick = onOpenFolderClick
        )
        favoriteAddScreen(onBackClick = navController::popBackStack)
    }
}
