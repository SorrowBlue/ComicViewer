package com.sorrowblue.comicviewer.app

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.sorrowblue.comicviewer.feature.authentication.Authentication
import com.sorrowblue.comicviewer.feature.authentication.AuthenticationScreen
import com.sorrowblue.comicviewer.feature.authentication.AuthenticationScreenNavigator
import com.sorrowblue.comicviewer.feature.authentication.ScreenType
import com.sorrowblue.comicviewer.feature.tutorial.TutorialScreen
import logcat.logcat
import org.koin.compose.viewmodel.koinViewModel

private const val TAG = "RootScreenWrapper"

@Composable
internal fun RootScreenWrapper(
    finishApp: () -> Unit,
    viewModel: MainViewModel = koinViewModel(),
    content: @Composable () -> Unit,
) {
    val isInitialized by viewModel.isInitialized.collectAsState()
    val state = rememberRootScreenWrapperState()

    LaunchedEffect(state.tutorialRequired, state.authStatus, isInitialized) {
        logcat(tag = TAG) {
            "tutorialRequired=${state.tutorialRequired}, authStatus=${state.authStatus}, isInitialized=$isInitialized"
        }
    }
    if (state.tutorialRequired) {
        TutorialScreen(navigator = state::onTutorialComplete)
        SideEffect {
            viewModel.shouldKeepSplash.value = false
        }
    } else {
        LifecycleEventEffect(Lifecycle.Event.ON_STOP, onEvent = state::onStop)
        if (isInitialized || state.authStatus is AuthStatus.NoAuthRequired || (state.authStatus is AuthStatus.AuthRequired && (state.authStatus as AuthStatus.AuthRequired).authed)) {
            content()
        }
        when (val authStatus = state.authStatus) {
            is AuthStatus.AuthRequired -> {
                AnimatedVisibility(
                    visible = !authStatus.authed || !isInitialized,
                    enter = slideInVertically { it },
                    exit = slideOutVertically { it }
                ) {
                    AuthenticationScreen(
                        route = Authentication(ScreenType.Authenticate),
                        navigator = remember {
                            object : AuthenticationScreenNavigator {
                                override fun navigateUp() = finishApp()
                                override fun onCompleted() = state.onAuthComplete()
                            }
                        }
                    )
                    SideEffect {
                        viewModel.shouldKeepSplash.value = false
                    }
                }
            }

            AuthStatus.NoAuthRequired -> Unit
            AuthStatus.Unknown -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}
