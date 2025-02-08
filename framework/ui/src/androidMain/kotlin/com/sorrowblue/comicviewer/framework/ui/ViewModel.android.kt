package com.sorrowblue.comicviewer.framework.ui

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
actual inline fun <reified T : ViewModel> sharedKoinViewModel(): T {
    return koinViewModel(viewModelStoreOwner = LocalActivity.current as ComponentActivity)
}
