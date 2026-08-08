/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.app

import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.ApplicationViewModel
import dev.zacsweers.metrox.viewmodel.metroViewModel

@Composable
actual fun AppContent(appGraph: AppGraph) {
    metroViewModel<ApplicationViewModel>()
}
