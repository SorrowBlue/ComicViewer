/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sorrowblue.comicviewer.app.AppGraph
import com.sorrowblue.comicviewer.app.ComicViewerUI
import com.sorrowblue.comicviewer.app.rememberComicViewerUIState
import com.sorrowblue.comicviewer.domain.model.settings.DarkMode
import com.sorrowblue.comicviewer.framework.common.Initializer
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import com.sorrowblue.comicviewer.framework.common.require
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalDarkMode
import dev.zacsweers.metrox.viewmodel.LocalMetroViewModelFactory
import dev.zacsweers.metrox.viewmodel.metroViewModel
import logcat.LogPriority
import logcat.logcat

@Composable
context(context: PlatformContext, appGraph: AppGraph)
fun Application(finishApp: () -> Unit) {
    CompositionLocalProvider(LocalMetroViewModelFactory provides appGraph.metroVmf) {
        val viewModel = metroViewModel<ApplicationViewModel>()
        val state = rememberComicViewerUIState()
        val darkMode by viewModel.displaySettings.collectAsStateWithLifecycle(DarkMode.DEVICE)
        CompositionLocalProvider(LocalDarkMode provides darkMode) {
            ComicViewerUI(finishApp = finishApp, state = state)
        }
        LaunchedEffect(Unit) {
            Initializer.initialize(
                context.require<InitializerContext.Factory>()
                    .createInitializerContext().initializer.toList(),
            )
        }
    }
}
