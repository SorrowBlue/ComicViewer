package com.sorrowblue.comicviewer.app

import android.annotation.SuppressLint
import android.app.Activity
import androidx.activity.ComponentActivity
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.ramcosta.composedestinations.scope.DestinationScope
import com.ramcosta.composedestinations.wrapper.DestinationWrapper
import com.sorrowblue.comicviewer.feature.authentication.Authentication
import com.sorrowblue.comicviewer.feature.authentication.AuthenticationScreen
import com.sorrowblue.comicviewer.feature.authentication.AuthenticationScreenNavigator
import com.sorrowblue.comicviewer.feature.authentication.Mode
import com.sorrowblue.comicviewer.feature.tutorial.TutorialScreen
import logcat.logcat
import org.koin.compose.viewmodel.koinViewModel

/**
 * Root screen wrapper
 *
 * @constructor Create empty Root screen wrapper
 */
internal object RootScreenWrapper : DestinationWrapper {

    @Composable
    override fun <T> DestinationScope<T>.Wrap(
        @SuppressLint("ComposableLambdaParameterNaming") screenContent: @Composable () -> Unit,
    ) {
        val mainViewModel =
            koinViewModel<MainViewModel>(viewModelStoreOwner = LocalContext.current as ComponentActivity)
        val isInitialized by mainViewModel.isInitialized.collectAsState()
        val state = rememberRootScreenWrapperState()

        LaunchedEffect(state.tutorialRequired, state.authStatus, isInitialized) {
            logcat(tag = RootScreenWrapper::class.simpleName) {
                "tutorialRequired=${state.tutorialRequired}, authStatus=${state.authStatus}, isInitialized=$isInitialized"
            }
        }
        if (state.tutorialRequired) {
            TutorialScreen(navigator = state::onTutorialComplete)
            SideEffect {
                mainViewModel.shouldKeepSplash.value = false
            }
        } else {
            LifecycleEventEffect(Lifecycle.Event.ON_PAUSE) {
                state.onStop()
            }
            if (isInitialized || state.authStatus is AuthStatus.NoAuthRequired || (state.authStatus is AuthStatus.AuthRequired && (state.authStatus as AuthStatus.AuthRequired).authed)) {
                screenContent()
            }
            when (val authStatus = state.authStatus) {
                is AuthStatus.AuthRequired -> {
                    AnimatedVisibility(
                        visible = !authStatus.authed || !isInitialized,
                        enter = slideInVertically { it },
                        exit = slideOutVertically { it }
                    ) {
                        val activity = LocalContext.current as Activity
                        AuthenticationScreen(
                            route = Authentication(Mode.Authentication),
                            navigator = object : AuthenticationScreenNavigator {
                                override fun navigateUp() {
                                    activity.finish()
                                }

                                override fun onCompleted() {
                                    state.onAuthComplete()
                                }
                            }
                        )
                        SideEffect {
                            mainViewModel.shouldKeepSplash.value = false
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
}
