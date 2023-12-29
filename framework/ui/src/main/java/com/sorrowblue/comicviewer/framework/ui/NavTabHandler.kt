package com.sorrowblue.comicviewer.framework.ui

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NavTabHandler @Inject constructor() : ViewModel() {
    var currentOnClick: (() -> Unit)? = null
}

@Composable
fun NavTabHandler(
    viewModelStoreOwner: ViewModelStoreOwner = LocalContext.current as ComponentActivity,
    viewModel: NavTabHandler = viewModel<NavTabHandler>(viewModelStoreOwner = viewModelStoreOwner),
    onClick: () -> Unit,
) {
    val currentOnClick by rememberUpdatedState(onClick)
    viewModel.currentOnClick = currentOnClick
    DisposableEffect(Unit) {
        onDispose {
            viewModel.currentOnClick = null
        }
    }
}
