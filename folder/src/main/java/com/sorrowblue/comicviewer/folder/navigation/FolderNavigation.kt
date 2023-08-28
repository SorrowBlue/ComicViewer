package com.sorrowblue.comicviewer.folder.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sorrowblue.comicviewer.domain.Base64.decodeFromBase64
import com.sorrowblue.comicviewer.domain.Base64.encodeToBase64
import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.folder.FolderRoute

private const val bookshelfIdArg = "bookshelfId"
private const val pathArg = "path"
private const val prefixArg = "prefix"

internal class FolderArgs(
    val bookshelfId: BookshelfId,
    val path: String,
    val prefix: String? = null
) {
    val base64Path get() = path.encodeToBase64()

    constructor(savedStateHandle: SavedStateHandle) :
            this(
                BookshelfId(checkNotNull(savedStateHandle[bookshelfIdArg])),
                (checkNotNull(savedStateHandle[pathArg]) as String).decodeFromBase64(),
                (savedStateHandle[prefixArg])
            )
}

fun NavGraphBuilder.folderScreen(
    prefix: String = "",
    navigateToSearch: (BookshelfId, String) -> Unit,
    onClickFile: (File) -> Unit,
    onSettingsClick: () -> Unit,
    onBackClick: () -> Unit,
    onAddFavoriteClick: (File) -> Unit,
) {
    composable(
        route = folderRoute(prefix),
        arguments = listOf(
            navArgument(bookshelfIdArg) { type = NavType.IntType },
            navArgument(pathArg) { type = NavType.StringType },
            navArgument(prefixArg) {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            }
        ),
        enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(700)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(700)
            )
            fadeOut()
        },
        popEnterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(700)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(700)
            )
        }

    ) {
        FolderRoute(
            onSearchClick = navigateToSearch,
            onAddFavoriteClick = onAddFavoriteClick,
            onClickFile = onClickFile,
            onSettingsClick = onSettingsClick,
            onBackClick = onBackClick,
        )
    }
}

const val FolderRoute = "folder"

fun folderRoute(prefix: String) =
    "$prefix/$FolderRoute/{$bookshelfIdArg}/{$pathArg}?prefix={$prefixArg}"

fun NavController.navigateToFolder(
    bookshelfId: BookshelfId,
    path: String,
    prefix: String = "",
    navOptions: NavOptions? = null
) {
    navigate(
        "$prefix/$FolderRoute/${bookshelfId.value}/${path.encodeToBase64()}?prefix=$prefix",
        navOptions
    )
}
