package com.sorrowblue.comicviewer.framework.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigation.suite.ExperimentalMaterial3AdaptiveNavigationSuiteApi
import androidx.compose.material3.adaptive.navigation.suite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigation.suite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigation.suite.NavigationSuiteScope
import androidx.compose.material3.adaptive.navigation.suite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme

@OptIn(
    ExperimentalMaterial3AdaptiveApi::class,
    ExperimentalMaterial3AdaptiveNavigationSuiteApi::class
)
@Composable
fun NavigationSuiteScaffold(
    showNavigation: Boolean,
    navigationSuiteItems: NavigationSuiteScope.() -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (PaddingValues) -> Unit,
) {
    if (showNavigation) {
        val navSuiteType =
            NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(currentWindowAdaptiveInfo())
        NavigationSuiteScaffold(
            containerColor = ComicTheme.colorScheme.surface,
            layoutType = navSuiteType,
            navigationSuiteItems = navigationSuiteItems,
            modifier = if (navSuiteType == NavigationSuiteType.NavigationBar) {
                modifier
            } else {
                modifier
                    .background(ComicTheme.colorScheme.surface)
                    .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Start))
            }
        ) {
            Scaffold(
                containerColor = ComicTheme.colorScheme.surface,
                contentWindowInsets = if (navSuiteType == NavigationSuiteType.NavigationBar) {
                    WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)
                } else {
                    WindowInsets.safeDrawing
                },
                content = content
            )
        }
    } else {
        Scaffold(
            containerColor = ComicTheme.colorScheme.surface,
            contentWindowInsets = WindowInsets.safeDrawing,
            content = content
        )
    }
}
