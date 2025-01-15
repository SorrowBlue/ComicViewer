package com.sorrowblue.comicviewer

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
internal actual fun RootScreenWrapper(
    finishApp: () -> Unit,
    content: @Composable () -> Unit,
) {
    val viewModel: MainViewModel = hiltViewModel<MainViewModel>()
    RootScreenWrapper(viewModel = viewModel, finishApp = finishApp) {
        content()
    }
}
