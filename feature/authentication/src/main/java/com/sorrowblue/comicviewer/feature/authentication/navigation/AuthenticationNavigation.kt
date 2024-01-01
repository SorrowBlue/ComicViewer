package com.sorrowblue.comicviewer.feature.authentication.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.utils.composable
import com.sorrowblue.comicviewer.feature.authentication.AuthenticationScreen
import com.sorrowblue.comicviewer.feature.authentication.destinations.AuthenticationScreenDestination

enum class Mode {
    Register,
    Change,
    Erase,
    Authentication,
}

class AuthenticationArgs(
    val mode: Mode,
    val handleBack: Boolean,
)

fun NavController.navigateToAuthentication(
    mode: Mode,
    handleBack: Boolean = false,
    navOptionsBuilder: NavOptionsBuilder.() -> Unit = {},
) {
    navigate(
        AuthenticationScreenDestination(mode, handleBack),
        navOptionsBuilder = navOptionsBuilder
    )
}

fun NavGraphBuilder.authenticationScreen(
    onBack: () -> Unit,
    onBackClick: () -> Unit,
    onAuthCompleted: (Boolean, Mode) -> Unit,
) {
    composable(AuthenticationScreenDestination) {
        AuthenticationScreen(
            args = navArgs,
            savedStateHandle = navBackStackEntry.savedStateHandle,
            onBack = onBack,
            onBackClick = onBackClick,
            onAuthCompleted = onAuthCompleted
        )
    }
}
