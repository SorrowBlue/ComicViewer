/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.framework.ui.material3

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.visible
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.BasicAlertDialogOverride
import androidx.compose.material3.BasicAlertDialogOverrideScope
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ComponentOverrideApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalBasicAlertDialogOverride
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.core.isCompactWindowClass
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewMultiplatform
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.preview.fake.LoremIpsum

internal val DialogMinWidth = 280.dp
internal val DialogMaxWidth = 560.dp

@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalMaterial3ComponentOverrideApi
object FixedDefaultBasicAlertDialogOverride : BasicAlertDialogOverride {
    @Composable
    override fun BasicAlertDialogOverrideScope.BasicAlertDialog() {
        Dialog(onDismissRequest = onDismissRequest, properties = properties) {
            val isFullScreenDialog = isCompactWindowClass()
            Box(
                modifier =
                    modifier.then(
                        if (isFullScreenDialog) {
                            Modifier
                        } else {
                            Modifier.sizeIn(
                                minWidth = DialogMinWidth,
                                maxWidth = DialogMaxWidth,
                            ).sizeIn(maxHeight = DialogMaxWidth)
                        },
                    ),
                propagateMinConstraints = true,
            ) {
                content()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ComponentOverrideApi::class)
@Composable
fun AdaptiveAlertDialog(
    title: @Composable () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    isFullScreenDialog: Boolean = isCompactWindowClass(),
    navigationIcon: @Composable () -> Unit = { BackIconButton(onClick = onBackClick) },
    content: @Composable ((PaddingValues) -> Unit),
) {
    val movableContent = remember {
        movableContentOf { contentPadding: PaddingValues -> content(contentPadding) }
    }
    CompositionLocalProvider(
        LocalBasicAlertDialogOverride provides FixedDefaultBasicAlertDialogOverride,
    ) {
        BasicAlertDialog(
            onDismissRequest = onBackClick,
            properties = DialogProperties(usePlatformDefaultWidth = false),
            modifier = modifier,
        ) {
            if (isFullScreenDialog) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = title,
                            navigationIcon = navigationIcon,
                            windowInsets = WindowInsets.safeDrawing.only(AppBarWindowInsets),
                        )
                    },
                    contentWindowInsets = WindowInsets.safeDrawing.add(ContentWindowInsets),
                    content = movableContent,
                )
            } else {
                AlertDialogContent(
                    title = title,
                    content = {
                        movableContent(it)
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ComponentOverrideApi::class)
@Composable
fun AdaptiveAlertDialog2(
    title: @Composable () -> Unit,
    onDismissRequest: () -> Unit,
    actionButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    confirmButton: @Composable (() -> Unit)? = null,
    dismissButton: @Composable (() -> Unit)? = null,
    isFullScreenDialog: Boolean = isCompactWindowClass(),
    navigationIcon: @Composable () -> Unit = {},
    content: @Composable ((PaddingValues) -> Unit),
) {
    val movableContent = remember {
        movableContentOf { contentPadding: PaddingValues -> content(contentPadding) }
    }
    CompositionLocalProvider(
        LocalBasicAlertDialogOverride provides FixedDefaultBasicAlertDialogOverride,
    ) {
        BasicAlertDialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(usePlatformDefaultWidth = false),
            modifier = modifier,
        ) {
            if (isFullScreenDialog) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = title,
                            navigationIcon = navigationIcon,
                            actions = {
                                actionButton()
                            },
                            windowInsets = WindowInsets.safeDrawing.only(AppBarWindowInsets),
                        )
                    },
                    contentWindowInsets = WindowInsets.safeDrawing.add(ContentWindowInsets),
                    content = movableContent,
                )
            } else {
                AlertDialogContent(
                    title = title,
                    confirmButton = confirmButton,
                    dismissButton = dismissButton,
                    content = {
                        movableContent(it)
                    },
                )
            }
        }
    }
}

private val ContentWindowInsets
    @Composable
    get() = ComicTheme.dimension.run {
        WindowInsets(left = margin, right = margin, bottom = margin)
    }

private val AppBarWindowInsets get() = WindowInsetsSides.Horizontal + WindowInsetsSides.Top

@PreviewMultiplatform
@Composable
private fun AdaptiveAlertDialogPreview() {
    PreviewTheme {
        Box(modifier = Modifier.fillMaxSize())
        AdaptiveAlertDialog2(
            title = { Text(text = "Adaptive Alert Dialog") },
            actionButton = {
                TextButton(onClick = { }) {
                    Text(text = "Confirm")
                }
            },
            confirmButton = {
                Button(onClick = { }) {
                    Text(text = "Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { }) {
                    Text(text = "Dismiss")
                }
            },
            onDismissRequest = { },
        ) { contentPadding ->
            val scrollState = rememberScrollState()
            val dividerAlpha by animateFloatAsState(
                targetValue = if (scrollState.canScrollForward) 1f else 0f,
                label = "DividerAlpha",
            )
            val dividerColor = ComicTheme.colorScheme.outlineVariant
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .verticalScroll(scrollState)
                    // drawBehind を使用して下端に直接描画
                    .drawBehind {
                        if (dividerAlpha > 0f) {
                            val strokeWidth = 1.dp.toPx()
                            // Y座標はコンポーネントの最下部から線の太さの半分を引いた位置
                            val y = size.height - strokeWidth / 2

                            drawLine(
                                color = dividerColor.copy(alpha = dividerAlpha),
                                start = Offset(0f, y),
                                end = Offset(size.width, y),
                                strokeWidth = strokeWidth,
                            )
                        }
                    },
            ) {
                LoremIpsum.forEach {
                    Text(text = it, modifier = Modifier.padding(vertical = 8.dp))
                }
            }
            HorizontalDivider(modifier = Modifier.visible(scrollState.canScrollForward))
        }
    }
}
