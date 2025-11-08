package com.sorrowblue.comicviewer.framework.ui

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

val LocalGlobalSnackbarState = staticCompositionLocalOf<GlobalSnackbarState> {
    error("No GlobalSnackbarState provided")
}

interface GlobalSnackbarState {
    val snackbarHostState: SnackbarHostState

    fun showSnackbar(
        message: String,
        actionLabel: String? = null,
        withDismissAction: Boolean = false,
        duration: SnackbarDuration =
            if (actionLabel == null) SnackbarDuration.Short else SnackbarDuration.Indefinite,
        onResult: suspend (SnackbarResult) -> Unit = {},
    )
}

@Composable
fun rememberGlobalSnackbarState(): GlobalSnackbarState {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    return remember(coroutineScope, snackbarHostState) {
        GlobalSnackbarStateImpl(coroutineScope, snackbarHostState)
    }
}

private class GlobalSnackbarStateImpl(
    val coroutineScope: CoroutineScope,
    override val snackbarHostState: SnackbarHostState,
) : GlobalSnackbarState {
    override fun showSnackbar(
        message: String,
        actionLabel: String?,
        withDismissAction: Boolean,
        duration: SnackbarDuration,
        onResult: suspend (SnackbarResult) -> Unit,
    ) {
        coroutineScope.launch {
            val result = snackbarHostState.showSnackbar(
                message = message,
                actionLabel = actionLabel,
                withDismissAction = withDismissAction,
                duration = duration,
            )
            onResult(result)
        }
    }
}
