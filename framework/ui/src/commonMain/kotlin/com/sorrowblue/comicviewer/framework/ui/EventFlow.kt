/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.framework.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import logcat.asLog
import logcat.logcat

typealias EventFlow<T> = MutableSharedFlow<T>

@Suppress("FunctionName")
fun <T> EventFlow() = MutableSharedFlow<T>(extraBufferCapacity = 20)

@Composable
fun <EVENT> EventEffect(
    eventFlow: SharedFlow<EVENT>,
    block: suspend CoroutineScope.(EVENT) -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    SafeLaunchedEffect(eventFlow) {
        supervisorScope {
            eventFlow.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { event ->
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
