/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.framework.ui.navigation3

import androidx.compose.runtime.staticCompositionLocalOf
import com.sorrowblue.comicviewer.framework.navigation.Navigator

val LocalNavigator = staticCompositionLocalOf<Navigator> {
    error("No AdaptiveNavigationSuiteState provided")
}
