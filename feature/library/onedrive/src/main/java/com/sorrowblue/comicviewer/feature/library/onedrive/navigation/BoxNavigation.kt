package com.sorrowblue.comicviewer.feature.library.onedrive.navigation

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sorrowblue.comicviewer.feature.library.onedrive.OneDriveRoute
import com.sorrowblue.comicviewer.feature.library.onedrive.data.oneDriveModule
import com.sorrowblue.comicviewer.feature.library.serviceloader.OneDriveNavigation
import org.koin.core.context.loadKoinModules

private const val DriveIdArg = "drive_id"
private const val ItemIdArg = "item_id"

internal class OneDriveArgs(val driveId: String?, val itemId: String?) {

    constructor(bundle: Bundle) : this(bundle.getString(DriveIdArg), bundle.getString(ItemIdArg))
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
            loadKoinModules(oneDriveModule)
            OneDriveRoute(
                args = OneDriveArgs(it.arguments!!),
                onBackClick = navController::popBackStack,
                onFolderClick = { folder -> navController.navigateToOneDrive(folder.path) },
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
