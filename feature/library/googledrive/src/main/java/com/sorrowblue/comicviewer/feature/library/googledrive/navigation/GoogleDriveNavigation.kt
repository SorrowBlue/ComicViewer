package com.sorrowblue.comicviewer.feature.library.googledrive.navigation

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sorrowblue.comicviewer.domain.model.Base64.decodeFromBase64
import com.sorrowblue.comicviewer.domain.model.Base64.encodeToBase64
import com.sorrowblue.comicviewer.feature.library.googledrive.GoogleDriveRoute
import com.sorrowblue.comicviewer.feature.library.googledrive.googleDriveModule
import com.sorrowblue.comicviewer.feature.library.serviceloader.GoogleDriveNavigation
import org.koin.core.context.loadKoinModules

private const val PathArg = "path"

internal class GoogleDriveArgs(val path: String) {

    constructor(arguments: Bundle) : this(
        checkNotNull(arguments.getString(PathArg)).decodeFromBase64()
    )
}

const val GoogleDriveRoute = "GoogleDrive"

object GoogleDriveNavigationImpl : GoogleDriveNavigation {

    override fun NavGraphBuilder.addOnScreen(navController: NavController) {
        composable(
            route = "$GoogleDriveRoute?path={$PathArg}",
            arguments = listOf(
                navArgument("pathArg") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) {
            loadKoinModules(googleDriveModule)
            GoogleDriveRoute(
                args = GoogleDriveArgs(it.arguments!!),
                onFileClick = { file ->
                    navController.navigateToGoogleDrive(file.path)
                },
                onBackClick = navController::popBackStack,
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
