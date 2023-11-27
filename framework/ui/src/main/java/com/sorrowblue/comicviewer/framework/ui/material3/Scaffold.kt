package com.sorrowblue.comicviewer.framework.ui.material3

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.preview.rememberMobile

@Composable
fun Scaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    snackbarHostState: SnackbarHostState? = null,
    floatingActionButton: @Composable () -> Unit = {},
    contentWindowInsets: WindowInsets = WindowInsets.safeDrawing,
    content: @Composable (PaddingValues) -> Unit,
) {
    androidx.compose.material3.Scaffold(
        topBar = topBar,
        snackbarHost = {
            snackbarHostState?.let {
                SnackbarHost(snackbarHostState.value)
            }
        },
        floatingActionButton = floatingActionButton,
        containerColor = if (rememberMobile()) ComicTheme.colorScheme.surface else ComicTheme.colorScheme.surfaceContainer,
        contentWindowInsets = contentWindowInsets,
        modifier = modifier,
        content = content
    )
}

@JvmInline
value class SnackbarHostState(
    val value: androidx.compose.material3.SnackbarHostState = androidx.compose.material3.SnackbarHostState(),
) {
    suspend fun showSnackbar(text: String) {
        value.showSnackbar(text)
    }
}

@Composable
fun rememberSnackbarHostState() = remember { SnackbarHostState() }
