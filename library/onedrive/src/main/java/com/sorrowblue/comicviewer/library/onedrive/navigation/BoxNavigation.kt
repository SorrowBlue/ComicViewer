package com.sorrowblue.comicviewer.library.onedrive.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sorrowblue.comicviewer.library.onedrive.OneDriveRoute
import com.sorrowblue.comicviewer.library.serviceloader.OneDriveNavigation

private const val driveIdArg = "drive_id"
private const val itemIdArg = "item_id"

internal class OneDriveArgs(val driveId: String?, val itemId: String?) {
    constructor(savedStateHandle: SavedStateHandle) :
            this(savedStateHandle[driveIdArg], savedStateHandle[itemIdArg])
}

private const val OneDriveRoute = "OneDrive"

object OneDriveNavigationImpl : OneDriveNavigation {

    override fun NavGraphBuilder.oneDriveScreen(navController: NavController) {
        composable(
            route = "$OneDriveRoute?drive_id={$driveIdArg}&item_id={$itemIdArg}",
            arguments = listOf(
                navArgument(driveIdArg) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument(itemIdArg) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
            )
        ) {
            OneDriveRoute(
                onBackClick = navController::popBackStack,
                onFolderClick = { navController.navigateToOneDrive(it.path) }
            )
        }
    }

    override fun NavController.navigateToOneDrive(driveId: String?, itemId: String?) {
        if (driveId == null) {
            navigate(OneDriveRoute)
        } else {
            navigate("$OneDriveRoute?drive_id=${driveId}&item_id=${itemId}")
        }
    }
}

class OneDriveNavigationProviderImpl : OneDriveNavigation.Provider {
    override fun get(): OneDriveNavigation {
        return OneDriveNavigationImpl
    }
}
