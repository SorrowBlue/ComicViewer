package com.sorrowblue.comicviewer.feature.library.box.navigation

import androidx.lifecycle.SavedStateHandle
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
import com.sorrowblue.comicviewer.feature.library.serviceloader.BoxNavigation

private const val PathArg = "path"

internal class BoxArgs(val path: String) {
    constructor(savedStateHandle: SavedStateHandle) : this(
        checkNotNull(savedStateHandle.get<String>(PathArg)).decodeFromBase64()
    )
}

private const val StateArg = "state"
private const val CodeArg = "code"

internal class BoxOauth2Args(val state: String, val code: String) {
    constructor(savedStateHandle: SavedStateHandle) : this(
        checkNotNull(savedStateHandle[StateArg]),
        checkNotNull(savedStateHandle[CodeArg])
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
            BoxRoute(
                onBackClick = navController::popBackStack,
                onFolderClick = { folder -> navController.navigateToBox(folder.path) }
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
                onComplete = {
                    navController.navigate(
                        BoxRoute,
                        navOptions {
                            popUpTo("BoxOauth2?state={$StateArg}&code={$CodeArg}") {
                                inclusive = true
                            }
                        }
                    )
                }
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
