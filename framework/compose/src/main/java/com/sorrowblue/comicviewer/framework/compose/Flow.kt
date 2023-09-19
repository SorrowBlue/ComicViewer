package com.sorrowblue.comicviewer.framework.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import logcat.logcat

@Composable
fun <T> Flow<T>.CollectAsEffect(
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend (T) -> Unit,
) {
    LaunchedEffect(key1 = Unit) {
        onEach(block).flowOn(context).launchIn(this)
    }
}

abstract class ComposeViewModel<T> : ViewModel() {

    private val _uiEvents: MutableStateFlow<List<T>> = MutableStateFlow(emptyList())
    val uiEvents: StateFlow<List<T>> get() = _uiEvents.asStateFlow()

    fun ComposeViewModel<T>.updateUiEvent(uiEvent: T) {
        _uiEvents.update { currentUiEvent -> currentUiEvent + uiEvent }
    }

    internal fun consumeUiEvent(uiEvent: T) {
        _uiEvents.update { currentMessages -> currentMessages.filterNot { it == uiEvent } }
    }
}

@Composable
fun <T> LaunchedEffectUiEvent(
    viewModel: ComposeViewModel<T>,
    onEvent: suspend (T) -> Unit,
) {
    LaunchedEffect(viewModel) {
        viewModel.uiEvents.collect { currentUiEvents ->
            if (currentUiEvents.isNotEmpty()) {
                val uiEvent = currentUiEvents[0]
                viewModel.consumeUiEvent(uiEvent)
                logcat("${viewModel::class.simpleName}") { "uiEvent=$uiEvent" }
                onEvent(uiEvent)
            }
        }
    }
}

@Composable
fun LifecycleEffect(
    lifecycle: Lifecycle = LocalLifecycleOwner.current.lifecycle,
    lifecycleObserver: LifecycleObserver,
) {
    DisposableEffect(lifecycle, lifecycleObserver) {
        lifecycle.addObserver(lifecycleObserver)
        onDispose { lifecycle.removeObserver(lifecycleObserver) }
    }
}
