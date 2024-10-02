package com.sorrowblue.comicviewer.framework.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

interface ScreenStateEvent<T> {

    val event: SharedFlow<T>

    val scope: CoroutineScope

    fun ScreenStateEvent<T>.sendEvent(event: T) {
        (this@sendEvent.event as? MutableSharedFlow)?.let {
            scope.launch {
                it.emit(event)
            }
        }
    }
}

@Composable
fun <T : Any> LaunchedEventEffect(event: SharedFlow<T>, onEvent: (T) -> Unit) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val currentOnEvent by rememberUpdatedState(onEvent)
    LaunchedEffect(event, lifecycle) {
        event.flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)
            .onEach {
                currentOnEvent(it)
            }
            .launchIn(this)
    }
}
