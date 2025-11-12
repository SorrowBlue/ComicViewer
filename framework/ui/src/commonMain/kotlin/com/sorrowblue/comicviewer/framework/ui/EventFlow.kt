package com.sorrowblue.comicviewer.framework.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import logcat.asLog
import logcat.logcat

typealias EventFlow<T> = MutableSharedFlow<T>

@Suppress("FunctionName")
fun <T> EventFlow() = MutableSharedFlow<T>(extraBufferCapacity = 20)

@Composable
fun <EVENT> EventEffect(
    eventFlow: EventFlow<EVENT>,
    block: suspend CoroutineScope.(EVENT) -> Unit,
) {
    SafeLaunchedEffect(eventFlow) {
        supervisorScope {
            eventFlow.collect { event ->
                launch {
                    block(event)
                }
            }
        }
    }
}

@Composable
fun SafeLaunchedEffect(key: Any?, block: suspend CoroutineScope.() -> Unit) {
    val composeEffectErrorHandler = LocalComposeEffectErrorHandler.current
    val currentBlock by rememberUpdatedState(block)
    LaunchedEffect(key) {
        runCatching {
            currentBlock()
        }.onFailure {
            ensureActive()
            logcat { it.asLog() }
            composeEffectErrorHandler.emit(it)
        }
    }
}

interface ComposeEffectErrorHandler {
    suspend fun emit(throwable: Throwable)
}

val LocalComposeEffectErrorHandler = staticCompositionLocalOf<ComposeEffectErrorHandler> {
    object : ComposeEffectErrorHandler {
        override suspend fun emit(throwable: Throwable) {
            throwable.printStackTrace()
        }
    }
}
