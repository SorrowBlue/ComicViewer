/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.settings.common

import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.framework.navigation.Navigator

fun interface SettingsDetailPlaceholder {

    @Composable
    operator fun invoke(navigator: Navigator)
}
