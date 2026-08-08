/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.sorrowblue.comicviewer.InitializerContext
import com.sorrowblue.comicviewer.framework.common.Initializer
import com.sorrowblue.comicviewer.framework.common.appGraph

@Composable
actual fun AppContent(appGraph: AppGraph) {
    LaunchedEffect(Unit) {
        Initializer.initialize(
            appGraph.context.appGraph<InitializerContext.Factory>()
                .createInitializerContext().initializer.toList(),
        )
    }
}
