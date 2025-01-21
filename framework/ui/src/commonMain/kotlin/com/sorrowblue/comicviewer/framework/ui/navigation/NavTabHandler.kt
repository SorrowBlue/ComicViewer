package com.sorrowblue.comicviewer.framework.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.compose.koinInject
import org.koin.core.annotation.Singleton

@Singleton
class NavTabHandler {
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
    viewModel: NavTabHandler = koinInject<NavTabHandler>(),
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
