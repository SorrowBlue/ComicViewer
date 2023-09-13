package com.sorrowblue.comicviewer.feature.library.dropbox.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sorrowblue.comicviewer.domain.Base64.decodeFromBase64
import com.sorrowblue.comicviewer.domain.Base64.encodeToBase64
import com.sorrowblue.comicviewer.feature.library.dropbox.DropBoxRoute
import com.sorrowblue.comicviewer.feature.library.serviceloader.DropBoxNavigation

private const val pathArg = "path"

internal class DropBoxArgs(val path: String) {
    constructor(savedStateHandle: SavedStateHandle) :
            this(checkNotNull(savedStateHandle.get<String>(pathArg)).decodeFromBase64())
}

private const val DropBoxRoute = "DropBox"

object DropBoxNavigationImpl : DropBoxNavigation {

    override fun NavGraphBuilder.addOnScreen(navController: NavController) {
        composable(
            route = "$DropBoxRoute?path={$pathArg}",
            arguments = listOf(navArgument(pathArg) {
                type = NavType.StringType
                defaultValue = ""
            })
        ) {
            DropBoxRoute(
                onBackClick = navController::popBackStack,
                onFolderClick = { navController.navigateToDropBox(it.path) }
            )
        }
    }

    override fun NavController.navigateToAddOnScreen() {
        navigateToDropBox()
    }

    private fun NavController.navigateToDropBox(path: String = "") =
        navigate("$DropBoxRoute?path=${path.encodeToBase64()}")
}

class DropBoxNavigationProviderImpl : DropBoxNavigation.Provider {
    override fun get(): DropBoxNavigation {
        return DropBoxNavigationImpl
    }
}
