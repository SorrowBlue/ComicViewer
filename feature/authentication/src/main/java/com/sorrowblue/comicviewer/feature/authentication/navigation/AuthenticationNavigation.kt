package com.sorrowblue.comicviewer.feature.authentication.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sorrowblue.comicviewer.feature.authentication.AuthenticationRoute

private const val HandleBackArg = "handleBack"
private const val ModeArg = "mode"

enum class Mode {
    Register,
    Change,
    Erase,
    Authentication,
}

internal class AuthenticationArgs(
    val handleBack: Boolean,
    val mode: Mode,
) {
    constructor(savedStateHandle: SavedStateHandle) : this(
        checkNotNull<Boolean>(savedStateHandle[HandleBackArg]),
        Mode.valueOf(checkNotNull(savedStateHandle[ModeArg])),
    )
}

private const val AuthenticationRouteBase = "authentication"
const val AuthenticationRoute = "$AuthenticationRouteBase/{$ModeArg}/?handleBack={$HandleBackArg}"

fun NavController.navigateToAuthentication(
    mode: Mode,
    handleBack: Boolean = false,
    navOptions: NavOptions? = null,
) {
    navigate("$AuthenticationRouteBase/${mode.name}/?handleBack=$handleBack", navOptions)
}

fun NavGraphBuilder.authenticationScreen(
    onBack: () -> Unit,
    onAuthCompleted: (Boolean, Mode) -> Unit,
) {
    composable(
        route = AuthenticationRoute,
        arguments = listOf(
            navArgument(ModeArg) { type = NavType.StringType },
            navArgument(HandleBackArg) { type = NavType.BoolType },
        )
    ) {
        AuthenticationRoute(onBack = onBack, onAuthCompleted = onAuthCompleted)
    }
}
