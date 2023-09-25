package com.sorrowblue.comicviewer.framework.ui.lifecycle

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import logcat.logcat

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
