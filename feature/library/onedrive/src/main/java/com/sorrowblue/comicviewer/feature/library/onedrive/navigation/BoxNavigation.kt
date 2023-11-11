package com.sorrowblue.comicviewer.feature.library.onedrive.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sorrowblue.comicviewer.feature.library.onedrive.OneDriveRoute
import com.sorrowblue.comicviewer.feature.library.serviceloader.OneDriveNavigation

private const val DriveIdArg = "drive_id"
private const val ItemIdArg = "item_id"

internal class OneDriveArgs(val driveId: String?, val itemId: String?) {
    constructor(savedStateHandle: SavedStateHandle) :
        this(savedStateHandle[DriveIdArg], savedStateHandle[ItemIdArg])
}

private const val OneDriveRoute = "OneDrive"

object OneDriveNavigationImpl : OneDriveNavigation {

    override fun NavGraphBuilder.addOnScreen(navController: NavController) {
        composable(
            route = "$OneDriveRoute?drive_id={$DriveIdArg}&item_id={$ItemIdArg}",
            arguments = listOf(
                navArgument(DriveIdArg) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument(ItemIdArg) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
            )
        ) {
            OneDriveRoute(
                onBackClick = navController::popBackStack,
                onFolderClick = { folder -> navController.navigateToOneDrive(folder.path) }
            )
        }
    }

    override fun NavController.navigateToAddOnScreen() {
        navigateToOneDrive()
    }

    private fun NavController.navigateToOneDrive(driveId: String? = null, itemId: String? = null) {
        if (driveId == null) {
            navigate(OneDriveRoute)
        } else {
            navigate("$OneDriveRoute?drive_id=$driveId&item_id=$itemId")
        }
    }
}

class OneDriveNavigationProviderImpl : OneDriveNavigation.Provider {
    override fun get(): OneDriveNavigation {
        return OneDriveNavigationImpl
    }
}
