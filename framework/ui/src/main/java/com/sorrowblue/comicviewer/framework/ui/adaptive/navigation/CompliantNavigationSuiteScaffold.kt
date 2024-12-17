/*
 * Copyright 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sorrowblue.comicviewer.framework.ui.adaptive.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowWidthSizeClass
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalContainerColor
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewMultiScreen
import com.sorrowblue.comicviewer.framework.ui.preview.ScratchBox
import com.sorrowblue.comicviewer.framework.ui.preview.nextLoremIpsum
import de.drick.compose.edgetoedgepreviewlib.EdgeToEdgeTemplate
import de.drick.compose.edgetoedgepreviewlib.InsetsConfig

@Immutable
sealed interface NavigationState {
    val visible: Boolean

    val suiteType: NavigationSuiteType
        get() = if (visible) {
            when (this) {
                is NavigationBar -> NavigationSuiteType.NavigationBar
                is NavigationRail -> NavigationSuiteType.NavigationRail
                is NavigationDrawer -> NavigationSuiteType.NavigationDrawer
            }
        } else {
            NavigationSuiteType.None
        }

    @Immutable
    data class NavigationBar(override val visible: Boolean) : NavigationState

    @Immutable
    data class NavigationRail(override val visible: Boolean) : NavigationState

    @Immutable
    data class NavigationDrawer(override val visible: Boolean) : NavigationState
}

val LocalNavigationState =
    compositionLocalOf<NavigationState> { NavigationState.NavigationBar(false) }

/**
 * Calculate from adaptive info
 *
 * @param adaptiveInfo
 * @param defaultVisible
 * @return [NavigationState]
 * @see [NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo]
 */
@Suppress("UnusedReceiverParameter")
fun NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo2(
    adaptiveInfo: WindowAdaptiveInfo,
    defaultVisible: Boolean = true,
): NavigationState = with(adaptiveInfo) {
    if (windowPosture.isTabletop) {
        NavigationState.NavigationBar(defaultVisible)
    } else if (
        windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED ||
        windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.MEDIUM
    ) {
        NavigationState.NavigationRail(defaultVisible)
    } else {
        NavigationState.NavigationBar(defaultVisible)
    }
}

/**
 * @see [androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold]
 */
@Composable
fun CompliantNavigationSuiteScaffold(
    navigationSuiteItems: androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScope.() -> Unit,
    modifier: Modifier = Modifier,
    navigationState: NavigationState =
        NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo2(currentWindowAdaptiveInfo()),
    content: @Composable () -> Unit,
) {
    val navigationSuiteColors = NavigationSuiteDefaults.colors()
    NavigationSuiteScaffold(
        containerColor = ComicTheme.colorScheme.surface,
        contentColor = ComicTheme.colorScheme.onSurface,
        navigationSuiteItems = navigationSuiteItems,
        layoutType = navigationState.suiteType,
        modifier = modifier
            .background(
                when (navigationState) {
                    is NavigationState.NavigationBar ->
                        navigationSuiteColors.navigationBarContainerColor

                    is NavigationState.NavigationDrawer ->
                        navigationSuiteColors.navigationDrawerContainerColor

                    is NavigationState.NavigationRail ->
                        navigationSuiteColors.navigationRailContainerColor
                }
            )
            .windowInsetsPadding(
                if (navigationState.visible) {
                    when (navigationState) {
                        is NavigationState.NavigationBar ->
                            WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom)

                        is NavigationState.NavigationRail ->
                            WindowInsets.safeDrawing.only(WindowInsetsSides.Start)

                        is NavigationState.NavigationDrawer ->
                            WindowInsets.safeDrawing
                                .only(WindowInsetsSides.Start)
                                .add(PermanentDrawerSheetAdditionalInsets)
                    }
                } else {
                    WindowInsets(0)
                }
            ),
        content = {
            CompositionLocalProvider(
                LocalContainerColor provides ComicTheme.colorScheme.surface,
                LocalNavigationState provides navigationState
            ) {
                content()
            }
        }
    )
}

/**
 * Horizontal [WindowInsets] to add to [PermanentDrawerSheet]
 */
private val PermanentDrawerSheetAdditionalInsets: WindowInsets
    @Composable get() = WindowInsets(left = 12.dp, right = 12.dp)

@PreviewMultiScreen
@Composable
private fun CompliantNavigationSuiteScaffoldPreview(@PreviewParameter(PreviewProvider::class) config: Config) {
    ComicTheme {
        EdgeToEdgeTemplate(
            cfg = InsetsConfig.GestureNav.copy(isInvertedOrientation = config.isInvertedOrientation),
            isLandscape = config.isLandscape,
        ) {
            CompliantNavigationSuiteScaffold(
                navigationSuiteItems = {
                    repeat(5) {
                        item(
                            selected = it == 1,
                            onClick = {},
                            icon = { Icon(ComicIcons.Edit, null) },
                            label = { Text(nextLoremIpsum().take(8)) }
                        )
                    }
                },
            ) {
                Scaffold(
                    contentWindowInsets = WindowInsets.safeDrawing,
                    containerColor = LocalContainerColor.current,
                ) { contentPadding ->
                    ScratchBox(
                        color = Color.Red,
                        modifier = Modifier
                            .padding(contentPadding)
                            .fillMaxSize()
                    )
                }
            }
        }
    }
}

private data class Config(
    val isLandscape: Boolean,
    val isInvertedOrientation: Boolean,
)

private class PreviewProvider : PreviewParameterProvider<Config> {
    override val values: Sequence<Config> = sequenceOf(
        Config(isLandscape = false, isInvertedOrientation = false),
        Config(isLandscape = false, isInvertedOrientation = true),
        Config(isLandscape = true, isInvertedOrientation = false),
        Config(isLandscape = true, isInvertedOrientation = true),
    )
}
