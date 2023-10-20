package com.sorrowblue.comicviewer.feature.settings.common

import android.content.res.Configuration
import androidx.annotation.IntRange
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Slider
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
import com.sorrowblue.comicviewer.framework.designsystem.theme.largeBottom
import com.sorrowblue.comicviewer.framework.designsystem.theme.largeTop
import com.sorrowblue.comicviewer.framework.ui.add
import com.sorrowblue.comicviewer.framework.ui.material3.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.preview.rememberMobile
import kotlinx.collections.immutable.PersistentList

interface SettingsItem {
    val title: Int
    val icon: ImageVector? get() = null
    val text: String? get() = null
}

@Composable
fun <T : SettingsItem> SettingsListContents(
    list: PersistentList<T>,
    onClick: (T) -> Unit,
    contentPadding: PaddingValues = PaddingValues(),
) {
    val isMobile = rememberMobile()
    val padding = if (isMobile) {
        contentPadding
    } else {
        contentPadding.add(
            paddingValues = PaddingValues(
                horizontal = ComicTheme.dimension.margin,
                vertical = ComicTheme.dimension.margin
            )
        )
    }
    LazyColumn(
        contentPadding = padding,
    ) {
        itemsIndexed(list, key = { index, _ -> index }) { index, item ->
            ListItem(
                headlineContent = { Text(stringResource(item.title)) },
                leadingContent = item.icon?.let {
                    { Icon(it, null) }
                },
                modifier = Modifier
                    .then(
                        if (isMobile) Modifier else when (index) {
                            0 -> Modifier.clip(ComicTheme.shapes.largeTop)
                            list.lastIndex -> Modifier.clip(ComicTheme.shapes.largeBottom)
                            else -> Modifier
                        }
                    )
                    .clickable(onClick = { onClick(item) }),
                supportingContent = item.text?.let {
                    { Text(text = it) }
                }
            )
        }
    }
}

@Composable
fun SettingsColumn(
    contentPadding: PaddingValues = PaddingValues(),
    content: @Composable ColumnScope.() -> Unit,
) {
    val isMobile = rememberMobile()
    val padding = if (isMobile) {
        contentPadding
    } else {
        contentPadding.add(
            paddingValues = PaddingValues(
                horizontal = ComicTheme.dimension.margin,
                vertical = ComicTheme.dimension.margin
            )
        )
    }
    Column {
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
) {
    ListItem(
        headlineContent = { title() },
        supportingContent = summary?.let { { it() } },
        leadingContent = icon?.let { { it() } },
        trailingContent = widget?.let { { it() } },
        modifier = Modifier
            .clickable(onClick = onClick)
            .then(modifier)
    )
}

@Composable
fun Setting(
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
        icon = icon?.let { { Icon(imageVector = it, contentDescription = null) } }
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
) {
    Box {
        Setting(
            title = title,
            summary = summary,
            icon = icon,
            widget = { Switch(checked = checked, onCheckedChange = onCheckedChange) },
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
fun SwitchSetting(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    summary: String? = null,
    icon: ImageVector? = null,
) {
    SwitchSetting(
        title = { Text(text = title) },
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        summary = summary?.let { { Text(text = it) } },
        icon = icon?.let { { Icon(imageVector = it, contentDescription = null) } }
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
fun SliderSetting(
    title: @Composable () -> Unit,
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    @IntRange(from = 0)
    steps: Int = 0,
    widget: @Composable (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null,
) {
    Setting(
        title = title,
        summary = {
            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = valueRange,
                steps = steps
            )
        },
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
    Column(modifier = modifier) {
        Box(
            Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            ProvideTextStyle(value = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.primary)) {
                title()
            }
        }
        content()
    }
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
                var media by remember { mutableFloatStateOf(0f) }
                SliderSetting(
                    title = { Text("メディアの音量") },
                    value = media, onValueChange = { media = it },
                    icon = { Icon(ComicIcons.MusicNote, null) },
                    modifier = Modifier.clickable { },
                )
                SettingsCategory(
                    title = {
                        Text("ディスプレイのロック")
                    }
                ) {
                    var checked by remember { mutableStateOf(false) }
                    SwitchSetting(
                        title = {
                            Text("電源ロックの音")
                        },
                        checked = checked,
                        onCheckedChange = { checked = it },
                    )

                    var checked2 by remember { mutableStateOf(false) }
                    SeparateSwitchSetting(
                        title = {
                            Text("電源ロックの音")
                        },
                        checked = checked2,
                        onCheckedChange = { checked2 = it },
                        onClick = {}
                    )
                }
                ListItem(
                    headlineContent = { Text("settings_label_tutorial") },
                    leadingContent = { Icon(ComicIcons.Start, null) },
                    modifier = Modifier.clickable { },
                )
            }
        }
    }
}
