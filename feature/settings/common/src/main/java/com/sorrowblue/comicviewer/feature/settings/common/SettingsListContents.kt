package com.sorrowblue.comicviewer.feature.settings.common

import android.content.res.Configuration
import androidx.annotation.IntRange
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.add
import com.sorrowblue.comicviewer.framework.ui.material3.CustomSlider
import com.sorrowblue.comicviewer.framework.ui.material3.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.preview.rememberMobile

@Composable
fun SettingsColumn(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    content: @Composable ColumnScope.() -> Unit,
) {
    val isMobile = rememberMobile()
    val padding = if (isMobile) {
        contentPadding
    } else {
        contentPadding.add(
            paddingValues = PaddingValues(
                horizontal = ComicTheme.dimension.margin, vertical = ComicTheme.dimension.margin
            )
        )
    }
    Column(
        Modifier
            .verticalScroll(rememberScrollState())
            .padding(padding)
            .then(if (isMobile) Modifier else Modifier.clip(ComicTheme.shapes.large))
            .then(modifier)
    ) {
        content()
    }
}

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
        colors = if (enabled) ListItemDefaults.colors() else ListItemDefaults.colors(
            headlineColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
            leadingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
            overlineColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
            supportingColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
            trailingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
        ),
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
    title: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    summary: Int? = null,
    icon: ImageVector? = null,
    enabled: Boolean = true,
) {
    Setting(
        title = stringResource(id = title),
        onClick = onClick,
        modifier = modifier,
        summary = summary?.let { stringResource(id = it) },
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
fun CheckedSetting(
    title: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    summary: Int? = null,
    icon: ImageVector? = null,
) {
    CheckedSetting(
        title = stringResource(id = title),
        onClick = onClick,
        modifier = modifier,
        summary = summary?.let { stringResource(id = summary) },
        icon = icon,
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
    Box {
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
            modifier = modifier,
            enabled = enabled,
        )
        Box(
            Modifier
                .matchParentSize()
                .clickable { onCheckedChange(!checked) })
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
    title: Int,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    summary: Int? = null,
    icon: ImageVector? = null,
    enabled: Boolean = true,
) {
    SwitchSetting(
        title = stringResource(id = title),
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        summary = summary?.let { stringResource(id = it) },
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
fun SeparateSwitchSetting(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    summary: String? = null,
    icon: ImageVector? = null,
) {
    SeparateSwitchSetting(
        title = { Text(text = title) },
        checked = checked,
        onCheckedChange = onCheckedChange,
        onClick = onClick,
        modifier = modifier,
        summary = summary?.let { { Text(text = it) } },
        icon = icon?.let { { Icon(imageVector = it, contentDescription = null) } }
    )
}

@Composable
fun CheckboxSetting(
    title: @Composable () -> Unit,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    summary: @Composable (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
) {
    Box {
        Setting(
            title = title,
            summary = summary,
            icon = icon,
            widget = {
                Checkbox(
                    checked = checked,
                    onCheckedChange = onCheckedChange,
                    enabled = enabled
                )
            },
            onClick = {},
            modifier = modifier,
        )
        Box(
            Modifier
                .matchParentSize()
                .clickable { onCheckedChange(!checked) })
    }
}

@Composable
fun CheckboxSetting(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    summary: String? = null,
    icon: ImageVector? = null,
    enabled: Boolean = true,
) {
    CheckboxSetting(
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
fun CheckboxSetting(
    title: Int,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    summary: Int? = null,
    icon: ImageVector? = null,
    enabled: Boolean = true,
) {
    CheckboxSetting(
        title = stringResource(id = title),
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        summary = summary?.let { stringResource(id = it) },
        icon = icon,
        enabled = enabled
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
    title: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    @IntRange(from = 0) steps: Int = 0,
    widget: @Composable (() -> Unit)? = null,
    icon: ImageVector? = null,
    enabled: Boolean = true,
) {
    SliderSetting(
        title = { Text(text = title) },
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        valueRange = valueRange,
        steps = steps,
        widget = widget,
        icon = icon?.let { { Icon(imageVector = icon, contentDescription = null) } },
        enabled = enabled
    )
}

@Composable
fun SliderSetting(
    title: Int,
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    @IntRange(from = 0) steps: Int = 0,
    widget: @Composable (() -> Unit)? = null,
    icon: ImageVector? = null,
    enabled: Boolean = true,
) {
    SliderSetting(
        title = stringResource(id = title),
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
            Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 8.dp)
        ) {
            ProvideTextStyle(MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.primary)) {
                title()
            }
        }
        content()
    }
}

@Composable
fun SettingsCategory(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    SettingsCategory(
        title = { Text(text = title) },
        modifier = modifier,
        content = content
    )
}

@Composable
fun SettingsCategory(
    title: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    SettingsCategory(
        title = stringResource(id = title),
        modifier = modifier,
        content = content
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
fun PreviewSettingsScreen() {
    PreviewTheme {
        Surface {
            Column {
                Setting(
                    title = "音とバイブレーション",
                    icon = ComicIcons.VolumeUp,
                    onClick = {},
                    summary = "音量、ハプティクス、サイレント　モード",
                )
                CheckedSetting(
                    title = "音とバイブレーション",
                    onClick = {},
                )
                var media by remember { mutableFloatStateOf(0f) }
                SliderSetting(
                    title = "メディアの音量",
                    value = media, onValueChange = { media = it },
                    icon = ComicIcons.MusicNote,
                    modifier = Modifier.clickable { },
                )
                SettingsCategory(title = "ディスプレイのロック") {
                    var checked by remember { mutableStateOf(false) }
                    SwitchSetting(
                        title = "電源ロックの音",
                        checked = checked,
                        onCheckedChange = { checked = it },
                    )

                    var checked2 by remember { mutableStateOf(false) }
                    SeparateSwitchSetting(
                        title = "電源ロックの音",
                        checked = checked2,
                        onCheckedChange = { checked2 = it },
                        onClick = {})
                }
                Setting(title = "settings_label_tutorial", onClick = {}, icon = ComicIcons.Start)
            }
        }
    }
}
