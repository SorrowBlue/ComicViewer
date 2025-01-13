package com.sorrowblue.comicviewer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import logcat.logcat

private const val TAG = "RootScreenWrapper"

@Composable
internal fun RootScreenWrapper(
    viewModel: MainViewModel,
    finishApp: () -> Unit,
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
//        TutorialScreen(navigator = state::onTutorialComplete)
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = { state.onTutorialComplete() }) {
                Text("onTutorialComplete")
            }
        }
        SideEffect {
            viewModel.shouldKeepSplash.value = false
        }
    } else {
//        LifecycleEventEffect(Lifecycle.Event.ON_PAUSE) {
//            state.onStop()
//        }
        if (isInitialized || state.authStatus is AuthStatus.NoAuthRequired || (state.authStatus is AuthStatus.AuthRequired && (state.authStatus as AuthStatus.AuthRequired).authed)) {
            content()
            // TODO Removed
            SideEffect {
                viewModel.shouldKeepSplash.value = false
            }
        }
        when (val authStatus = state.authStatus) {
            is AuthStatus.AuthRequired -> {
                AnimatedVisibility(
                    visible = !authStatus.authed || !isInitialized,
                    enter = slideInVertically { it },
                    exit = slideOutVertically { it }
                ) {
//                    val activity = LocalContext.current as Activity
//                    AuthenticationScreen(
//                        args = AuthenticationArgs(Mode.Authentication),
//                        navigator = object : AuthenticationScreenNavigator {
//                            override fun navigateUp() {
//                                activity.finish()
//                            }
//
//                            override fun onCompleted() {
//                                state.onAuthComplete()
//                            }
//                        }
//                    )
                    Column(
                        Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(onClick = { finishApp() }) {
                            Text("navigateUp")
                        }
                        Button(onClick = { state.onAuthComplete() }) {
                            Text("onCompleted")
                        }
                    }
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
