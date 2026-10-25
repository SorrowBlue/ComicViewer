/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.authentication

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.github.skydoves.navgraph.annotations.NavDestination
import com.github.skydoves.navgraph.annotations.NavPreview
import com.sorrowblue.comicviewer.feature.authentication.nav.AuthenticationNavKey
import com.sorrowblue.comicviewer.feature.authentication.section.AuthenticationColumnContents
import com.sorrowblue.comicviewer.feature.authentication.section.AuthenticationRowContents
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.core.DetectOrientation
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewMultiplatform
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import comicviewer.feature.authentication.generated.resources.Res
import comicviewer.feature.authentication.generated.resources.authentication_error_incorrect_pin
import comicviewer.feature.authentication.generated.resources.authentication_error_pin_4_more
import comicviewer.feature.authentication.generated.resources.authentication_error_pin_not_match
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource

@Serializable
internal sealed interface AuthenticationScreenUiState {
    val error: ErrorType?
    val loading: Boolean

    @Serializable
    sealed interface Register : AuthenticationScreenUiState {
        @Serializable
        data class Input(
            override val error: ErrorType? = null,
            override val loading: Boolean = false,
        ) : Register

        @Serializable
        data class Confirm(
            override val error: ErrorType? = null,
            override val loading: Boolean = false,
        ) : Register
    }

    @Serializable
    data class Authentication(
        override val error: ErrorType? = null,
        override val loading: Boolean = false,
    ) : AuthenticationScreenUiState

    @Serializable
    sealed interface Change : AuthenticationScreenUiState {
        @Serializable
        data class ConfirmOld(
            override val error: ErrorType? = null,
            override val loading: Boolean = false,
        ) : Change

        @Serializable
        data class Input(
            override val error: ErrorType? = null,
            override val loading: Boolean = false,
        ) : Change

        @Serializable
        data class Confirm(
            override val error: ErrorType? = null,
            override val loading: Boolean = false,
        ) : Change
    }

    @Serializable
    data class Erase(
        override val error: ErrorType? = null,
        override val loading: Boolean = false,
    ) : AuthenticationScreenUiState
}

internal enum class ErrorType(val resource: StringResource) {
    IncorrectPin(Res.string.authentication_error_incorrect_pin),
    Pin4More(Res.string.authentication_error_pin_4_more),
    PinNotMatch(Res.string.authentication_error_pin_not_match),
}

@NavDestination(AuthenticationNavKey::class)
@Composable
internal fun AuthenticationScreen(
    uiState: AuthenticationScreenUiState,
    pinState: TextFieldState,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    DetectOrientation(modifier.fillMaxSize()) { isLandscape ->
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = ComicTheme.colorScheme.surfaceContainer,
            contentWindowInsets = WindowInsets.safeDrawing,
        ) { contentPadding ->
            if (isLandscape) {
                AuthenticationRowContents(
                    uiState = uiState,
                    pinState = pinState,
                    onBackClick = onBackClick,
                    onNextClick = onNextClick,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding)
                        .padding(ComicTheme.dimension.margin),
                )
            } else {
                AuthenticationColumnContents(
                    uiState = uiState,
                    pinState = pinState,
                    onBackClick = onBackClick,
                    onNextClick = onNextClick,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding)
                        .padding(ComicTheme.dimension.margin),
                )
            }
        }
    }
}

@NavPreview(AuthenticationNavKey::class)
@Composable
@PreviewMultiplatform
internal fun AuthenticationScreenPreview() = PreviewTheme {
    AuthenticationScreen(
        uiState = AuthenticationScreenUiState.Register.Input(),
        pinState = rememberTextFieldState("1234"),
        snackbarHostState = remember { SnackbarHostState() },
        onBackClick = {},
        onNextClick = {},
    )
}
