/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.authentication

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sorrowblue.comicviewer.feature.authentication.nav.ScreenType
import com.sorrowblue.comicviewer.framework.navigation.AppContentDecoratorProvider
import com.sorrowblue.comicviewer.framework.ui.LocalFinishApp
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet

@ContributesIntoSet(AppScope::class)
internal class AuthenticationAppContentDecoratorProvider(private val authSession: AuthSession) :
    AppContentDecoratorProvider {

    override val order = 1

    @Composable
    override fun Content(
        isInitialized: Boolean,
        onInitialize: () -> Unit,
        content: @Composable () -> Unit,
    ) {
        LifecycleEventEffect(Lifecycle.Event.ON_PAUSE, onEvent = authSession::onPause)
        AuthGuardEntryContent(
            authSession = authSession,
            isInitialized = isInitialized,
            onInitialize = onInitialize,
            content = content,
        )
    }
}

@Composable
private fun AuthGuardEntryContent(
    authSession: AuthSession,
    isInitialized: Boolean,
    onInitialize: () -> Unit,
    content: @Composable () -> Unit,
) {
    val currentOnInitialize by rememberUpdatedState(onInitialize)
    val authState by authSession.authState.collectAsStateWithLifecycle()
    val finishApp = LocalFinishApp.current
    if (authState is AuthState.NoAuthRequired ||
        (authState as? AuthState.AuthRequired)?.authed == true
    ) {
        content()
    }
    val isVisible = when (val state = authState) {
        is AuthState.AuthRequired -> !state.authed || !isInitialized

        AuthState.NoAuthRequired,
        AuthState.Loading,
        -> false
    }
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically { it },
        exit = slideOutVertically { it },
    ) {
        AuthenticationScreenRoot(
            screenType = ScreenType.Authenticate,
            onBackClick = finishApp,
            onComplete = authSession::onAuthComplete,
        )
        SideEffect(Unit) {
            currentOnInitialize()
        }
    }
}
