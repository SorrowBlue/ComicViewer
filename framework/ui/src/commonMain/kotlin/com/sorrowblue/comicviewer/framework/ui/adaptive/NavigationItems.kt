/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.framework.ui.adaptive

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.sorrowblue.comicviewer.framework.ui.navigation3.LocalNavigator

interface NavigationItems {

    @Composable
    fun Content(onNavigationReSelect: () -> Unit)
}

val LocalNavigationItems = staticCompositionLocalOf<NavigationItems> { DefaultNavigationItems }

private object DefaultNavigationItems : NavigationItems {
    @Composable
    override fun Content(onNavigationReSelect: () -> Unit) {
        val navigator = LocalNavigator.current
        navigator.topLevelRoutes.forEach { key ->
            val isSelected = key == navigator.topLevelKey
            NavigationSuiteItem(
                selected = isSelected,
                label = { Text(key.title) },
                icon = { Icon(key.icon, null) },
                onClick = {
                    if (key == navigator.topLevelKey) {
                        onNavigationReSelect()
                    } else {
                        navigator.navigate(key)
                    }
                },
                modifier = Modifier.testTag("NavigationSuiteItem"),
            )
        }
    }
}
