package com.sorrowblue.comicviewer.framework.ui

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.framework.ui.component.CloseIconButton

val WindowSizeClass.isCompact get() = widthSizeClass == WindowWidthSizeClass.Compact || heightSizeClass == WindowHeightSizeClass.Compact

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun ResponsiveDialogScaffold(
    title: @Composable () -> Unit,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
    confirmButton: (@Composable () -> Unit)? = null,
    snackbarHost: @Composable () -> Unit = {},
    contentPadding: PaddingValues = PaddingValues(),
    widthSizeClass: WindowSizeClass = LocalWindowAdaptiveInfo.current.windowSizeClass,
    scrollableState: ScrollableState? = null,
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
    content: @Composable (PaddingValues) -> Unit,
) {
    if (widthSizeClass.isCompact) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = title,
                    navigationIcon = { CloseIconButton(onClick = onCloseClick) },
                    actions = { confirmButton?.invoke() },
                    scrollBehavior = scrollBehavior
                )
            },
            snackbarHost = snackbarHost,
            contentWindowInsets = contentPadding.asWindowInsets(),
            modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
        ) { innerPadding ->
            content(innerPadding.add(paddingValues = PaddingValues(16.dp)))
        }
    } else {
        AlertDialog(
            onDismissRequest = onCloseClick,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    title()
                    Spacer(modifier = Modifier.weight(1f))
                    CloseIconButton(onClick = onCloseClick)
                }
            },
            text = {
                Column {
                    HorizontalDivider(Modifier.alpha(if (scrollableState?.canScrollBackward == true) 1f else 0f))
                    content(PaddingValues())
                    HorizontalDivider(Modifier.alpha(if (scrollableState?.canScrollForward == true) 1f else 0f))
                }
            },
            confirmButton = { confirmButton?.invoke() },
            dismissButton = {
                TextButton(onClick = onCloseClick) {
                    Text(text = stringResource(id = android.R.string.cancel))
                }
            }
        )
    }
}
