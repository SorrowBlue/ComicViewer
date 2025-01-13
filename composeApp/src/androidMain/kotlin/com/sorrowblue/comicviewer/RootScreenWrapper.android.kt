package com.sorrowblue.comicviewer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
internal actual fun RootScreenWrapper(
    finishApp: () -> Unit,
    content: @Composable () -> Unit,
) {
    val viewModel: MainViewModel = hiltViewModel<MainViewModel>()
    RootScreenWrapper(viewModel = viewModel, finishApp = finishApp) {
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Main content")
        }
    }
}
