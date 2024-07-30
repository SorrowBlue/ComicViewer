package com.sorrowblue.comicviewer.framework.ui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
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
