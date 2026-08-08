/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sorrowblue.comicviewer.app.AppGraph
import com.sorrowblue.comicviewer.app.ComicViewerApp
import com.sorrowblue.comicviewer.app.rememberComicViewerUIState
import com.sorrowblue.comicviewer.domain.model.settings.DarkMode
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalDarkMode
import dev.zacsweers.metrox.viewmodel.LocalMetroViewModelFactory
import dev.zacsweers.metrox.viewmodel.metroViewModel

@Composable
context(appGraph: AppGraph)
fun Application(finishApp: () -> Unit) {
    CompositionLocalProvider(LocalMetroViewModelFactory provides appGraph.viewModelFactory) {
        val viewModel = metroViewModel<ApplicationViewModel>()
        val state = rememberComicViewerUIState()
        val darkMode by viewModel.displaySettings.collectAsStateWithLifecycle(DarkMode.DEVICE)
        CompositionLocalProvider(LocalDarkMode provides darkMode) {
            ComicViewerApp(finishApp = finishApp, state = state)
        }
    }
}
