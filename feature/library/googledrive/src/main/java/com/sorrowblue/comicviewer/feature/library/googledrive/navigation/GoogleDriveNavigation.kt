package com.sorrowblue.comicviewer.feature.library.googledrive.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sorrowblue.comicviewer.domain.Base64.decodeFromBase64
import com.sorrowblue.comicviewer.domain.Base64.encodeToBase64
import com.sorrowblue.comicviewer.feature.library.googledrive.GoogleDriveRoute
import com.sorrowblue.comicviewer.feature.library.serviceloader.GoogleDriveNavigation

private const val pathArg = "path"

internal class GoogleDriveArgs(val path: String) {
    constructor(savedStateHandle: SavedStateHandle) :
            this(checkNotNull(savedStateHandle.get<String>(pathArg)).decodeFromBase64())
}

const val GoogleDriveRoute = "GoogleDrive"

object GoogleDriveNavigationImpl : GoogleDriveNavigation {

    override fun NavGraphBuilder.addOnScreen(navController: NavController) {
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

    override fun NavController.navigateToAddOnScreen() {
        navigateToGoogleDrive()
    }

    private fun NavController.navigateToGoogleDrive(path: String = "root") {
        navigate("$GoogleDriveRoute?path=${path.encodeToBase64()}")
    }
}

class GoogleDriveNavigationProviderImpl : GoogleDriveNavigation.Provider {
    override fun get() = GoogleDriveNavigationImpl
}
