package com.sorrowblue.comicviewer.feature.authentication.navigation

import android.os.Bundle
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

    constructor(bundle: Bundle) : this(
        bundle.getBoolean(HandleBackArg),
        Mode.valueOf(checkNotNull(bundle.getString(ModeArg))),
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
    onBackClick: () -> Unit,
    onAuthCompleted: (Boolean, Mode) -> Unit,
) {
    composable(
        route = AuthenticationRoute,
        arguments = listOf(
            navArgument(ModeArg) { type = NavType.StringType },
            navArgument(HandleBackArg) { type = NavType.BoolType },
        )
    ) {
        with(it) {
            AuthenticationRoute(
                onBack = onBack,
                onBackClick = onBackClick,
                onAuthCompleted = onAuthCompleted
            )
        }
    }
}
