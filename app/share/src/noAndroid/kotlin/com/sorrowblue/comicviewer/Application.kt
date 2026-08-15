/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sorrowblue.comicviewer.app.ComicViewerApp
import com.sorrowblue.comicviewer.domain.model.settings.DarkMode
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalDarkMode
import dev.zacsweers.metrox.viewmodel.metroViewModel

@Composable
context(context: PlatformContext)
fun Application(finishApp: () -> Unit) {
    val appViewModel = metroViewModel<ApplicationViewModel>()
    val darkMode by appViewModel.displaySettings.collectAsStateWithLifecycle(DarkMode.DEVICE)
    CompositionLocalProvider(LocalDarkMode provides darkMode) {
        ComicViewerApp(
            finishApp = finishApp,
        )
    }
}
