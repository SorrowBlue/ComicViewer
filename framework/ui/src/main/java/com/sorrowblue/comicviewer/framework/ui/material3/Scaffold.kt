package com.sorrowblue.comicviewer.framework.ui.material3

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
fun Scaffold(
    topBar: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState? = null,
    contentWindowInsets: WindowInsets = WindowInsets.safeDrawing,
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        topBar = topBar,
        snackbarHost = {
            snackbarHostState?.let {
                SnackbarHost(snackbarHostState.value)
            }
        },
        contentWindowInsets = contentWindowInsets,
        modifier = modifier,
        content = content
    )
}

@JvmInline
value class SnackbarHostState(val value: androidx.compose.material3.SnackbarHostState = androidx.compose.material3.SnackbarHostState()) {
    suspend fun showSnackbar(text: String) {
        value.showSnackbar(text)
    }
}

@Composable
fun rememberSnackbarHostState() = remember { SnackbarHostState() }
