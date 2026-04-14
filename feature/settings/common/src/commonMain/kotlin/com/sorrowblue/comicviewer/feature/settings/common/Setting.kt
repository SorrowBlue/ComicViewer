package com.sorrowblue.comicviewer.feature.settings.common

import androidx.annotation.IntRange
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.Label
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme

@Composable
fun Setting(
    title: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    summary: @Composable (() -> Unit)? = null,
    widget: @Composable (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
) {
    ListItem(
        headlineContent = { title() },
        supportingContent = summary?.let { { it() } },
        leadingContent = icon?.let { { it() } },
        trailingContent = widget?.let { { it() } },
        modifier = Modifier
            .then(modifier)
            .clickable(enabled = enabled, onClick = onClick),
        colors = if (enabled) {
            ListItemDefaults.colors()
        } else {
            ListItemDefaults.colors(
                headlineColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                leadingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                overlineColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                supportingColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                trailingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
            )
        },
    )
}

@Composable
fun SwitchSetting(
    title: @Composable () -> Unit,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    summary: @Composable (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
) {
    Box(modifier = modifier) {
        Setting(
            title = title,
            summary = summary,
            icon = icon,
            widget = {
                Switch(
                    checked = checked,
                    onCheckedChange = onCheckedChange,
                    enabled = enabled,
                )
            },
            onClick = {},
            enabled = enabled,
        )
        Box(
            Modifier
                .matchParentSize()
                .clickable(enabled = enabled) { onCheckedChange(!checked) },
        )
    }
}

@Composable
fun SeparateSwitchSetting(
    title: @Composable () -> Unit,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    summary: @Composable (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
) {
    Setting(
        title = title,
        summary = summary,
        icon = icon,
        widget = {
            Row(Modifier.height(IntrinsicSize.Max)) {
                VerticalDivider()
                Spacer(Modifier.size(16.dp))
                Switch(checked = checked, onCheckedChange = onCheckedChange)
            }
        },
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
    )
}

@Composable
fun SliderSetting(
    title: @Composable () -> Unit,
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    thumbText: @Composable (Float) -> Unit = SettingsDefault.Thumb,
    colors: SliderColors = SliderDefaults.colors(),
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    @IntRange(from = 0) steps: Int = 0,
    widget: @Composable (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    Setting(
        title = title,
        summary = {
            Slider(
                value = value,
                onValueChange = { onValueChange(it) },
                valueRange = valueRange,
                steps = steps,
                colors = colors,
                thumb = {
                    val isDragged by interactionSource.collectIsDraggedAsState()
                    val isPressed by interactionSource.collectIsPressedAsState()
                    Label(
                        label = {
                            PlainTooltip {
                                thumbText(value)
                            }
                        },
                        isPersistent = isDragged || isPressed,
                        interactionSource = interactionSource,
                    ) {
                        SliderDefaults.Thumb(
                            interactionSource = interactionSource,
                            colors = colors,
                            enabled = enabled,
                        )
                    }
                },
                interactionSource = interactionSource,
                enabled = enabled,
            )
        },
        enabled = enabled,
        icon = icon,
        widget = widget,
        onClick = {},
        modifier = modifier,
    )
}

@Composable
fun SettingsCategory(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(modifier = modifier.background(ComicTheme.colorScheme.surface)) {
        Box(
            Modifier.padding(
                start = ComicTheme.dimension.padding * 2,
                top = ComicTheme.dimension.padding * 3,
                end = ComicTheme.dimension.padding * 2,
                bottom = ComicTheme.dimension.padding,
            ),
        ) {
            ProvideTextStyle(
                MaterialTheme.typography.titleSmall.copy(color = MaterialTheme.colorScheme.primary),
            ) {
                title()
            }
        }
        content()
    }
}

object SettingsDefault {
    val Thumb: @Composable (Float) -> Unit = { value ->
        Text(
            text = value.toString(),
            modifier = Modifier.widthIn(min = 40.dp),
            textAlign = TextAlign.Center,
        )
    }
}

@Preview
@Composable
private fun SettingsPreview() {
    PreviewTheme {
        SettingsCategory(
            title = { Text("Category Title") },
        ) {
            Setting(
                title = { Text("Setting Title") },
                summary = { Text("This is a summary of the setting.") },
                icon = { Icon(ComicIcons.Info, null) },
                onClick = {},
            )
            SwitchSetting(
                title = { Text("Setting Title") },
                summary = { Text("This is a summary of the setting.") },
                icon = { Icon(ComicIcons.Info, null) },
                checked = true,
                onCheckedChange = {},
                enabled = true,
            )
            SeparateSwitchSetting(
                title = { Text("Setting Title") },
                summary = { Text("This is a summary of the setting.") },
                icon = { Icon(ComicIcons.Info, null) },
                checked = true,
                onCheckedChange = {},
                onClick = {},
                enabled = true,
            )
            SliderSetting(
                title = { Text("Setting Title") },
                icon = { Icon(ComicIcons.Info, null) },
                value = 0.5f,
                valueRange = 0f..1f,
                onValueChange = {},
                enabled = true,
            )
        }
    }
}
