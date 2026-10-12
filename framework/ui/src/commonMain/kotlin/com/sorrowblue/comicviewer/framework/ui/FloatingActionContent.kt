/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.framework.ui

import androidx.compose.material3.FloatingActionButtonElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import com.sorrowblue.comicviewer.framework.ui.adaptive.FloatingActionButtonState
import kotlin.reflect.KClass

interface FloatingActionContent {
    val navKey: KClass<out NavKey>
    val allowRail: Boolean get() = true

    @Composable
    fun Content(
        state: FloatingActionButtonState,
        modifier: Modifier,
        elevation: FloatingActionButtonElevation,
        expanded: Boolean,
    )
}
