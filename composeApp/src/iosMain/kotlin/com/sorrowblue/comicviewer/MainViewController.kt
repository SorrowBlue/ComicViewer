package com.sorrowblue.comicviewer

import androidx.compose.ui.window.ComposeUIViewController
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import platform.posix.exit

fun MainViewController() = ComposeUIViewController {
    val viewmodel = viewModel<MainViewModel>()
    RootScreenWrapper(viewModel = viewmodel, finishApp = { exit(0) }) {
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Main content")
        }
    }
}
