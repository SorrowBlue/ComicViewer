/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.app

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.adaptive.LocalNavigationItems
import com.sorrowblue.comicviewer.framework.ui.adaptive.LocalNavigationSuiteScaffoldState
import com.sorrowblue.comicviewer.framework.ui.adaptive.NavigationReSelectRegistry
import com.sorrowblue.comicviewer.framework.ui.adaptive.ProvideNavigationSuiteScaffoldWithPrimaryActionOverride
import com.sorrowblue.comicviewer.framework.ui.adaptive.currentNavigationSuiteType
import com.sorrowblue.comicviewer.framework.ui.adaptive.provideNavigationReSelectRegistry
import com.sorrowblue.comicviewer.framework.ui.animation.LocalSharedTransitionScope

@Composable
internal fun AppNavigationSuiteScaffold(
    state: NavigationSuiteScaffoldState,
    railActionContent: @Composable () -> Unit,
    bottomActionContent: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    val registry = remember { NavigationReSelectRegistry() }
    SharedTransitionLayout(modifier = Modifier.background(ComicTheme.colorScheme.background)) {
        CompositionLocalProvider(
            LocalNavigationSuiteScaffoldState provides state,
            provideNavigationReSelectRegistry(registry),
            LocalSharedTransitionScope provides this,
            ProvideNavigationSuiteScaffoldWithPrimaryActionOverride,
        ) {
            val navigationSuiteType = currentNavigationSuiteType()
            NavigationSuiteScaffold(
                navigationSuiteType = navigationSuiteType,
                state = state,
                navigationItems = {
                    LocalNavigationItems.current.Content(registry::onReSelect)
                },
                primaryActionContent = railActionContent,
            ) {
                val snackbarHostState = remember { SnackbarHostState() }
                AppSnackbarEffect(snackbarHostState)
                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    floatingActionButton = {
                        Box(contentAlignment = Alignment.BottomEnd) {
                            bottomActionContent()
                        }
                    },
                ) {
                    content()
                }
            }
        }
    }
}
