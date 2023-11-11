package com.sorrowblue.comicviewer.feature.library.dropbox.navigation

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sorrowblue.comicviewer.app.appModule
import com.sorrowblue.comicviewer.domain.model.Base64.decodeFromBase64
import com.sorrowblue.comicviewer.domain.model.Base64.encodeToBase64
import com.sorrowblue.comicviewer.feature.library.dropbox.DropBoxRoute
import com.sorrowblue.comicviewer.feature.library.dropbox.data.dropBoxModule
import com.sorrowblue.comicviewer.feature.library.serviceloader.DropBoxNavigation
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.context.loadKoinModules

private const val PathArg = "path"

internal class DropBoxArgs(val path: String) {
    constructor(bundle: Bundle) :
        this(checkNotNull(bundle.getString(PathArg)).decodeFromBase64())
}

private const val DropBoxRoute = "DropBox"

object DropBoxNavigationImpl : DropBoxNavigation {

    @OptIn(KoinExperimentalAPI::class)
    override fun NavGraphBuilder.addOnScreen(navController: NavController) {
        composable(
            route = "$DropBoxRoute?path={$PathArg}",
            arguments = listOf(
                navArgument(PathArg) {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) {
            KoinAndroidContext {
                loadKoinModules(listOf(appModule, dropBoxModule))
                DropBoxRoute(
                    args = DropBoxArgs(it.arguments!!),
                    onBackClick = navController::popBackStack,
                    onFolderClick = { folder -> navController.navigateToDropBox(folder.path) }
                )
            }
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
