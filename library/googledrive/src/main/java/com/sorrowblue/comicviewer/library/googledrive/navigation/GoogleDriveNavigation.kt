package com.sorrowblue.comicviewer.library.googledrive.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sorrowblue.comicviewer.domain.Base64.decodeFromBase64
import com.sorrowblue.comicviewer.domain.Base64.encodeToBase64
import com.sorrowblue.comicviewer.library.googledrive.GoogleDriveRoute


private const val pathArg = "path"

internal class GoogleDriveArgs(val path: String?) {
    constructor(savedStateHandle: SavedStateHandle) :
            this(savedStateHandle.get<String?>(pathArg)?.decodeFromBase64())
}

const val GoogleDriveRoute = "GoogleDrive"

fun NavGraphBuilder.googleDriveScreen(navController: NavController) {
    composable(
        route = "$GoogleDriveRoute?path={$pathArg}",
        arguments = listOf(navArgument("pathArg") {
            type = NavType.StringType
            nullable = true
            defaultValue = null
        })
    ) {
        GoogleDriveRoute(
            onFileClick = {
                navController.navigateToGoogleDrive(it.path)
            }
        )
    }
}

fun NavController.navigateToGoogleDrive(path: String? = null) {
    path?.let {
        navigate("$GoogleDriveRoute?path=${path.encodeToBase64()}")
    } ?: navigate(GoogleDriveRoute)
}
