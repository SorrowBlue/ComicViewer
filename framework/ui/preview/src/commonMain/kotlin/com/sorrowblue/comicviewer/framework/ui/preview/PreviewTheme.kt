/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.framework.ui.preview

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import coil3.annotation.ExperimentalCoilApi
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.AppState
import com.sorrowblue.comicviewer.framework.ui.LocalAppState
import com.sorrowblue.comicviewer.framework.ui.SnackbarEvent
import com.sorrowblue.comicviewer.framework.ui.adaptive.LocalNavigationItems
import com.sorrowblue.comicviewer.framework.ui.adaptive.NavigationItems
import com.sorrowblue.comicviewer.framework.ui.animation.LocalSharedTransitionScope
import de.drick.compose.edgetoedgepreviewlib.CameraCutoutMode
import de.drick.compose.edgetoedgepreviewlib.EdgeToEdgeTemplate
import de.drick.compose.edgetoedgepreviewlib.InsetMode
import de.drick.compose.edgetoedgepreviewlib.NavigationMode
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

@OptIn(ExperimentalCoilApi::class)
@Composable
fun PreviewTheme(
    modifier: Modifier = Modifier,
    showDeviceUi: Boolean = false,
    showInsetsBorder: Boolean = false,
    content: @Composable () -> Unit,
) {
    val movableContent = remember {
        movableContentOf {
            SharedTransitionLayout {
                CompositionLocalProvider(
                    ProvidesAppState,
                    provideAsyncImagePreviewHandler,
                    ProvidesPreviewNavigationItems,
                    LocalSharedTransitionScope provides this,
                ) {
                    AnimatedContent(true, modifier = modifier) {
                        if (it) {
                            CompositionLocalProvider(LocalNavAnimatedContentScope provides this) {
                                ComicTheme {
                                    content()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    if (showDeviceUi) {
        EdgeToEdgeTemplate(
            navMode = NavigationMode.Gesture,
            navigationBarMode = InsetMode.Visible,
            statusBarMode = InsetMode.Visible,
            cameraCutoutMode = CameraCutoutMode.Middle,
            showInsetsBorder = showInsetsBorder,
        ) {
            movableContent()
        }
    } else {
        movableContent()
    }
}

private val ProvidesPreviewNavigationItems
    get() = LocalNavigationItems provides PreviewNavigationItems

private val PreviewNavigationItems = object : NavigationItems {
    @Composable
    override fun Content(onNavigationReSelect: () -> Unit) {
        repeat(4) {
            NavigationSuiteItem(
                selected = true,
                onClick = {},
                icon = {
                    Icon(ComicIcons.Favorite, null)
                },
                label = {
                    Text("label")
                },
            )
        }
    }
}

private val ProvidesAppState
    @Composable
    get() = LocalAppState provides rememberPreviewAppState()

@Composable
private fun rememberPreviewAppState(): AppState {
    val appState = remember {
        PreviewAppState()
    }
    return appState
}

private class PreviewAppState : AppState {
    override val snackbarEvents: SharedFlow<SnackbarEvent>
        field = MutableSharedFlow<SnackbarEvent>(extraBufferCapacity = 16)

    override fun showSnackbar(
        message: String,
        actionLabel: String?,
        duration: SnackbarDuration,
        withDismissAction: Boolean,
        onActionPerformed: (() -> Unit)?,
    ) {
        snackbarEvents.tryEmit(
            SnackbarEvent(
                message = message,
                actionLabel = actionLabel,
                duration = duration,
                withDismissAction = withDismissAction,
                onActionPerformed = onActionPerformed,
            ),
        )
    }
}
