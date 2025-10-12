package com.sorrowblue.comicviewer.framework.ui.material3

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.ScrollbarBox
import androidx.compose.foundation.VerticalScrollbarBox
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.scrollbarStyle
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.takeOrElse
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme

@Composable
fun AlertDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    title: @Composable (() -> Unit)? = null,
    content: @Composable (PaddingValues) -> Unit,
) {
    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier
    ) {
        Surface(
            shape = AlertDialogDefaults.shape,
            color = AlertDialogDefaults.containerColor,
            tonalElevation = AlertDialogDefaults.TonalElevation,
        ) {
            Column {
                if (title != null) {
                    Box(
                        modifier = Modifier.padding(
                            start = 24.dp,
                            top = 24.dp,
                            end = 24.dp,
                            bottom = 16.dp
                        )
                    ) {
                        CompositionLocalProvider(LocalTextStyle provides ComicTheme.typography.headlineSmall) {
                            title()
                        }
                    }
                }
                content(PaddingValues(start = 24.dp, end = 24.dp, bottom = 24.dp))
            }
        }
    }
}

@Composable
fun AlertDialogContent(
    modifier: Modifier = Modifier,
    confirmButton: @Composable (() -> Unit)? = null,
    dismissButton: @Composable (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null,
    title: @Composable (() -> Unit)? = null,
    scrollableState: ScrollableState? = null,
    shape: Shape = AlertDialogDefaults.shape,
    containerColor: Color = AlertDialogDefaults.containerColor,
    iconContentColor: Color = AlertDialogDefaults.iconContentColor,
    titleContentColor: Color = AlertDialogDefaults.titleContentColor,
    textContentColor: Color = AlertDialogDefaults.textContentColor,
    tonalElevation: Dp = AlertDialogDefaults.TonalElevation,
    content: @Composable (() -> Unit)? = null,
) {
    AlertDialogContent(
        modifier = modifier,
        buttons = if (confirmButton != null || dismissButton != null) {
            {
                val buttonPaddingFromMICS =
                    LocalMinimumInteractiveComponentSize.current.takeOrElse { 0.dp } -
                        ButtonDefaults.MinHeight
                AlertDialogFlowRow(
                    mainAxisSpacing = ButtonsMainAxisSpacing,
                    crossAxisSpacing =
                    (ButtonsCrossAxisSpacing - buttonPaddingFromMICS).coerceIn(
                        0.dp,
                        ButtonsCrossAxisSpacing,
                    ),
                ) {
                    confirmButton?.invoke()
                    dismissButton?.invoke()
                }
            }
        } else {
            null
        },
        icon = icon,
        title = title,
        content = content,
        scrollableState = scrollableState,
        shape = shape,
        containerColor = containerColor,
        tonalElevation = tonalElevation,
        // Note that a button content color is provided here from the dialog's token, but in
        // most cases, TextButtons should be used for dismiss and confirm buttons. TextButtons
        // will not consume this provided content color value, and will used their own defined
        // or default colors.
        buttonContentColor = ComicTheme.colorScheme.primary,
        iconContentColor = iconContentColor,
        titleContentColor = titleContentColor,
        textContentColor = textContentColor,
    )
}

@Composable
private fun AlertDialogContent(
    buttons: (@Composable () -> Unit)?,
    icon: (@Composable () -> Unit)?,
    title: (@Composable () -> Unit)?,
    scrollableState: ScrollableState?,
    shape: Shape,
    containerColor: Color,
    tonalElevation: Dp,
    buttonContentColor: Color,
    iconContentColor: Color,
    titleContentColor: Color,
    textContentColor: Color,
    modifier: Modifier = Modifier,
    content: @Composable (() -> Unit)?,
) {
    Surface(
        modifier = modifier,
        shape = shape,
        color = containerColor,
        tonalElevation = tonalElevation,
    ) {
        Column(modifier = Modifier.padding(top = DialogSpacing, bottom = DialogSpacing)) {
            icon?.let {
                CompositionLocalProvider(LocalContentColor provides iconContentColor) {
                    Box(
                        Modifier.padding(DialogPaddingHorizonal)
                            .padding(IconPadding)
                            .align(Alignment.CenterHorizontally)
                    ) {
                        icon()
                    }
                }
            }
            title?.let {
                ProvideContentColorTextStyle(
                    contentColor = titleContentColor,
                    textStyle = ComicTheme.typography.headlineSmall,
                ) {
                    Box(
                        // Align the title to the center when an icon is present.
                        Modifier.padding(DialogPaddingHorizonal)
                            .padding(TitlePadding)
                            .align(
                                if (icon == null) {
                                    Alignment.Start
                                } else {
                                    Alignment.CenterHorizontally
                                }
                            )
                    ) {
                        title()
                    }
                }
            }
            content?.let {
                scrollableState?.let {
                    AnimatedVisibility(visible = scrollableState.canScrollBackward) {
                        HorizontalDivider()
                    }
                }
                val textStyle = ComicTheme.typography.bodyMedium
                ProvideContentColorTextStyle(
                    contentColor = textContentColor,
                    textStyle = textStyle,
                ) {
                    Box(
                        Modifier
                            .weight(weight = 1f, fill = false)
                            .align(Alignment.Start)
                    ) {
                        scrollableState?.let {
                            CompositionLocalProvider(LocalScrollbarStyle provides AlertDialogDefaults.scrollbarStyle()) {
                                when (scrollableState) {
                                    is LazyListState -> {
                                        ScrollbarBox(state = scrollableState) {
                                            content()
                                        }
                                    }

                                    is LazyGridState -> {
                                        ScrollbarBox(state = scrollableState) {
                                            content()
                                        }
                                    }

                                    is ScrollState -> {
                                        VerticalScrollbarBox(state = scrollableState) {
                                            Column(
                                                Modifier
                                                    .padding(DialogPaddingHorizonal)
                                                    .verticalScroll(scrollableState)
                                            ) {
                                                content()
                                            }
                                        }
                                    }
                                }
                            }
                        } ?: run {
                            Box(Modifier.padding(DialogPaddingHorizonal)) {
                                content()
                            }
                        }
                    }
                }
                scrollableState?.let {
                    AnimatedVisibility(visible = scrollableState.canScrollForward) {
                        HorizontalDivider()
                    }
                }
                Spacer(Modifier.padding(TextPadding))
            }
            buttons?.let {
                Box(modifier = Modifier.padding(DialogPaddingHorizonal).align(Alignment.End)) {
                    val textStyle = ComicTheme.typography.labelLarge
                    ProvideContentColorTextStyle(
                        contentColor = buttonContentColor,
                        textStyle = textStyle,
                        content = buttons,
                    )
                }
            }
        }
    }
}

val AlertDialogDefaults.dialogPaddingHorizonal get() = DialogPaddingHorizonal

private val DialogSpacing = 24.dp
private val DialogPaddingHorizonal = PaddingValues(horizontal = DialogSpacing)
private val IconPadding = PaddingValues(bottom = 16.dp)
private val TitlePadding = PaddingValues(bottom = 16.dp)
private val TextPadding = PaddingValues(bottom = 24.dp)
private val ButtonsMainAxisSpacing = 8.dp
private val ButtonsCrossAxisSpacing = 8.dp

@Composable
internal fun ProvideContentColorTextStyle(
    contentColor: Color,
    textStyle: TextStyle,
    content: @Composable () -> Unit,
) {
    val mergedStyle = LocalTextStyle.current.merge(textStyle)
    CompositionLocalProvider(
        LocalContentColor provides contentColor,
        LocalTextStyle provides mergedStyle,
        content = content,
    )
}

@Composable
internal fun AlertDialogFlowRow(
    mainAxisSpacing: Dp,
    crossAxisSpacing: Dp,
    content: @Composable () -> Unit,
) {
    val originalLayoutDirection = LocalLayoutDirection.current
    // The confirm button comes BEFORE the dismiss button when stacked vertically,
    // but AFTER the dismiss button when stacked horizontally.
    CompositionLocalProvider(LocalLayoutDirection provides originalLayoutDirection.flip()) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(mainAxisSpacing),
            verticalArrangement = Arrangement.spacedBy(crossAxisSpacing),
        ) {
            CompositionLocalProvider(
                LocalLayoutDirection provides originalLayoutDirection,
                content = content,
            )
        }
    }
}

private fun LayoutDirection.flip(): LayoutDirection =
    when (this) {
        LayoutDirection.Ltr -> LayoutDirection.Rtl
        LayoutDirection.Rtl -> LayoutDirection.Ltr
    }
