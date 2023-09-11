package com.sorrowblue.comicviewer.library.box.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navOptions
import com.sorrowblue.comicviewer.domain.Base64.decodeFromBase64
import com.sorrowblue.comicviewer.domain.Base64.encodeToBase64
import com.sorrowblue.comicviewer.library.box.BoxOauth2Route
import com.sorrowblue.comicviewer.library.box.BoxRoute
import com.sorrowblue.comicviewer.library.serviceloader.BoxNavigation

private const val pathArg = "path"

internal class BoxArgs(val path: String) {
    constructor(savedStateHandle: SavedStateHandle) :
            this(checkNotNull(savedStateHandle.get<String>(pathArg)).decodeFromBase64())
}

private const val stateArg = "state"
private const val codeArg = "code"

internal class BoxOauth2Args(val state: String, val code: String) {
    constructor(savedStateHandle: SavedStateHandle) :
            this(checkNotNull(savedStateHandle[stateArg]), checkNotNull(savedStateHandle[codeArg]))
}

private const val BoxRoute = "Box"

object BoxNavigationImpl : BoxNavigation {

    override fun NavGraphBuilder.boxScreen(navController: NavController) {
        composable(
            route = "$BoxRoute?path={$pathArg}",
            arguments = listOf(navArgument(pathArg) {
                type = NavType.StringType
                defaultValue = ""
            })
        ) {
            BoxRoute(
                onBackClick = navController::popBackStack,
                onFolderClick = { navController.navigateToBox(it.path) }
            )
        }
        composable(
            route = "BoxOauth2?state={$stateArg}&code={$codeArg}",
            arguments = listOf(
                navArgument(stateArg) {
                    type = NavType.StringType
                },
                navArgument(codeArg) {
                    type = NavType.StringType
                },
            ),
            deepLinks = listOf(
                NavDeepLink("https://comicviewer.sorrowblue.com/box/oauth2?state={state}&code={code}")
            )
        ) {
            BoxOauth2Route(
                onComplete = {
                    navController.navigate(BoxRoute, navOptions {
                        popUpTo("BoxOauth2?state={$stateArg}&code={$codeArg}") {
                            inclusive = true
                        }
                    })
                }
            )
        }
    }

    override fun NavController.navigateToBox(path: String) =
        navigate("$BoxRoute?path=${path.encodeToBase64()}")
}

class BoxNavigationProviderImpl : BoxNavigation.Provider {
    override fun get(): BoxNavigation {
        return BoxNavigationImpl
    }
}
