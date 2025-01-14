package com.sorrowblue.comicviewer

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
internal actual fun RootScreenWrapper(
    finishApp: () -> Unit,
    content: @Composable () -> Unit,
) {
    RootScreenWrapper(viewModel = viewModel<MainViewModel> { MainViewModel() }, finishApp = finishApp, content = content)
}
