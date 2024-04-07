package com.sorrowblue.comicviewer.framework.ui

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@HiltViewModel
class NavTabHandler @Inject constructor() : ViewModel() {
    val click = MutableSharedFlow<Unit>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
}

@Composable
fun NavTabHandler(
    lifecycle: Lifecycle = LocalLifecycleOwner.current.lifecycle,
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModelStoreOwner: ViewModelStoreOwner = LocalContext.current as ComponentActivity,
    viewModel: NavTabHandler = hiltViewModel<NavTabHandler>(viewModelStoreOwner = viewModelStoreOwner),
    onClick: () -> Unit,
) {
    val currentOnClick by rememberUpdatedState(onClick)
    LaunchedEffect(Unit) {
        viewModel.click
            .onEach { currentOnClick() }
            .flowWithLifecycle(lifecycle)
            .launchIn(scope)
    }
}
