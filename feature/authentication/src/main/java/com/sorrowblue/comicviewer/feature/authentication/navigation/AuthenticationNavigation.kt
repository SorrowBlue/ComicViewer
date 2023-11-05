package com.sorrowblue.comicviewer.feature.authentication.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sorrowblue.comicviewer.feature.authentication.AuthenticationRoute

private const val handleBackArg = "handleBack"
private const val modeArg = "mode"

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
        checkNotNull<Boolean>(savedStateHandle[handleBackArg]),
        Mode.valueOf(checkNotNull(savedStateHandle[modeArg])),
    )
}

private const val authenticationRouteBase = "authentication"
const val authenticationRoute = "$authenticationRouteBase/{$modeArg}/?handleBack={$handleBackArg}"

fun NavController.navigateToAuthentication(
    mode: Mode,
    handleBack: Boolean = false,
    navOptions: NavOptions? = null,
) {
    navigate("$authenticationRouteBase/${mode.name}/?handleBack=$handleBack", navOptions)
}

fun NavGraphBuilder.authenticationScreen(
    onBack: () -> Unit,
    onAuthCompleted: (Boolean, Mode) -> Unit,
) {
    composable(
        route = authenticationRoute,
        arguments = listOf(
            navArgument(modeArg) { type = NavType.StringType },
            navArgument(handleBackArg) { type = NavType.BoolType },
        )
    ) {
        AuthenticationRoute(onBack = onBack, onAuthCompleted = onAuthCompleted)
    }
}
