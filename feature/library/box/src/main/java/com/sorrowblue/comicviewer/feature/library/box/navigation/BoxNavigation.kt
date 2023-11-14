package com.sorrowblue.comicviewer.feature.library.box.navigation

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.navigation.navOptions
import com.sorrowblue.comicviewer.domain.model.Base64.decodeFromBase64
import com.sorrowblue.comicviewer.domain.model.Base64.encodeToBase64
import com.sorrowblue.comicviewer.feature.library.box.BoxOauth2Route
import com.sorrowblue.comicviewer.feature.library.box.BoxRoute
import com.sorrowblue.comicviewer.feature.library.box.data.boxModule
import com.sorrowblue.comicviewer.feature.library.serviceloader.BoxNavigation
import org.koin.core.context.loadKoinModules

private const val PathArg = "path"

internal class BoxArgs(val path: String) {

    constructor(bundle: Bundle) : this(
        checkNotNull(bundle.getString(PathArg)).decodeFromBase64()
    )
}

private const val StateArg = "state"
private const val CodeArg = "code"

internal class BoxOauth2Args(val state: String, val code: String) {

    constructor(bundle: Bundle) : this(
        checkNotNull(bundle.getString(StateArg)),
        checkNotNull(bundle.getString(CodeArg))
    )
}

private const val BoxRoute = "Box"

object BoxNavigationImpl : BoxNavigation {

    override fun NavGraphBuilder.addOnScreen(navController: NavController) {
        composable(
            route = "$BoxRoute?path={$PathArg}",
            arguments = listOf(
                navArgument(PathArg) {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) {
            loadKoinModules(boxModule)
            BoxRoute(
                args = BoxArgs(it.arguments!!),
                onBackClick = navController::popBackStack,
                onFolderClick = { folder -> navController.navigateToBox(folder.path) },
            )
        }
        composable(
            route = "BoxOauth2?state={$StateArg}&code={$CodeArg}",
            arguments = listOf(
                navArgument(StateArg) {
                    type = NavType.StringType
                },
                navArgument(CodeArg) {
                    type = NavType.StringType
                },
            ),
            deepLinks = listOf(
                navDeepLink {
                    uriPattern =
                        "https://comicviewer.sorrowblue.com/box/oauth2?state={state}&code={code}"
                }
            )
        ) {
            BoxOauth2Route(
                args = BoxOauth2Args(it.arguments!!),
                onComplete = {
                    navController.navigate(
                        BoxRoute,
                        navOptions {
                            popUpTo("BoxOauth2?state={$StateArg}&code={$CodeArg}") {
                                inclusive = true
                            }
                        }
                    )
                },
            )
        }
    }

    override fun NavController.navigateToAddOnScreen() {
        navigateToBox()
    }

    private fun NavController.navigateToBox(path: String = "") =
        navigate("$BoxRoute?path=${path.encodeToBase64()}")
}

class BoxNavigationProviderImpl : BoxNavigation.Provider {
    override fun get(): BoxNavigation {
        return BoxNavigationImpl
    }
}
