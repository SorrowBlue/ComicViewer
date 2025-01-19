package com.sorrowblue.comicviewer

import androidx.compose.runtime.Composable
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal actual fun RootScreenWrapper(
    finishApp: () -> Unit,
    content: @Composable () -> Unit,
) {
    val viewModel: MainViewModel = koinViewModel()
    RootScreenWrapper(viewModel = viewModel, finishApp = finishApp) {
        content()
    }
}
