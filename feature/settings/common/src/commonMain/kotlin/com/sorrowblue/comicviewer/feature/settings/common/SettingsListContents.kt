package com.sorrowblue.comicviewer.feature.settings.common

import androidx.annotation.IntRange
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.material3.CustomSlider
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

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
            .clickable(onClick = onClick),
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
fun Setting(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    summary: String? = null,
    icon: ImageVector? = null,
    enabled: Boolean = true,
) {
    Setting(
        title = { Text(text = title) },
        onClick = onClick,
        modifier = modifier,
        summary = summary?.let { { Text(text = it) } },
        icon = icon?.let { { Icon(imageVector = it, contentDescription = null) } },
        enabled = enabled
    )
}

@Composable
fun Setting(
    title: StringResource,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    summary: StringResource? = null,
    icon: ImageVector? = null,
    enabled: Boolean = true,
) {
    Setting(
        title = stringResource(title),
        onClick = onClick,
        modifier = modifier,
        summary = summary?.let { stringResource(it) },
        icon = icon,
        enabled = enabled
    )
}

@Composable
fun CheckedSetting(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    summary: String? = null,
    icon: ImageVector? = null,
) {
    Setting(
        title = { Text(text = title) },
        onClick = onClick,
        modifier = modifier,
        summary = summary?.let { { Text(text = it) } },
        icon = icon?.let { { Icon(imageVector = it, contentDescription = null) } },
        widget = { Icon(imageVector = ComicIcons.Check, contentDescription = null) }
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
                    enabled = enabled
                )
            },
            onClick = {},
            enabled = enabled,
        )
        Box(
            Modifier
                .matchParentSize()
                .clickable { onCheckedChange(!checked) }
        )
    }
}

@Composable
fun SwitchSetting(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    summary: String? = null,
    icon: ImageVector? = null,
    enabled: Boolean = true,
) {
    SwitchSetting(
        title = { Text(text = title) },
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        summary = summary?.let { { Text(text = it) } },
        icon = icon?.let { { Icon(imageVector = it, contentDescription = null) } },
        enabled = enabled
    )
}

@Composable
fun SwitchSetting(
    title: StringResource,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    summary: StringResource? = null,
    icon: ImageVector? = null,
    enabled: Boolean = true,
) {
    SwitchSetting(
        title = stringResource(title),
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        summary = summary?.let { stringResource(it) },
        icon = icon,
        enabled = enabled
    )
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
) {
    Setting(
        title = {
            Row(Modifier.height(IntrinsicSize.Max)) {
                title()
                Spacer(Modifier.weight(1f))
                VerticalDivider()
            }
        },
        summary = summary,
        icon = icon,
        widget = {
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        },
        onClick = onClick,
        modifier = modifier,
    )
}

@Composable
fun SliderSetting(
    title: @Composable () -> Unit,
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    @IntRange(from = 0) steps: Int = 0,
    widget: @Composable (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
) {
    Setting(
        title = title,
        summary = {
            CustomSlider(
                value = value,
                onValueChange = onValueChange,
                valueRange = valueRange,
                steps = steps,
                thumbLabel = { it.toString() },
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
fun SliderSetting(
    title: @Composable () -> Unit,
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: kotlin.ranges.IntRange = 0..100,
    @IntRange(from = 0) steps: Int = 0,
    widget: @Composable (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
) {
    Setting(
        title = title,
        summary = {
            CustomSlider(
                value = value.toFloat(),
                onValueChange = { onValueChange(it.toInt()) },
                valueRange = valueRange.first.toFloat()..valueRange.last.toFloat(),
                steps = steps,
                thumbLabel = { it.toInt().toString() },
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
fun SliderSetting(
    title: StringResource,
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    @IntRange(from = 0) steps: Int = 0,
    widget: @Composable (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
) {
    SliderSetting(
        title = { Text(text = stringResource(title)) },
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        valueRange = valueRange,
        steps = steps,
        widget = widget,
        icon = icon,
        enabled = enabled
    )
}

@Composable
fun SliderSetting(
    title: StringResource,
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: kotlin.ranges.IntRange = 0..100,
    @IntRange(from = 0) steps: Int = 0,
    widget: @Composable (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
) {
    SliderSetting(
        title = { Text(text = stringResource(title)) },
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        valueRange = valueRange,
        steps = steps,
        widget = widget,
        icon = icon,
        enabled = enabled
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
                bottom = ComicTheme.dimension.padding
            )
        ) {
            ProvideTextStyle(MaterialTheme.typography.titleSmall.copy(color = MaterialTheme.colorScheme.primary)) {
                title()
            }
        }
        content()
    }
}

@Composable
fun SettingsCategory(
    title: StringResource,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    SettingsCategory(
        title = { Text(text = stringResource(title)) },
        modifier = modifier,
        content = content
    )
}
