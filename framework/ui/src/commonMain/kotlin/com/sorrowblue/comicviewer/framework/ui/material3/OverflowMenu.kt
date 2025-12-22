package com.sorrowblue.comicviewer.framework.ui.material3

import androidx.compose.material3.AppBarRowScope
import androidx.compose.material3.AppBarScope
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import comicviewer.framework.ui.generated.resources.Res
import comicviewer.framework.ui.generated.resources.label_settings
import org.jetbrains.compose.resources.stringResource

fun AppBarScope.clickableItem(
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    label: @Composable () -> String,
    enabled: Boolean = true,
    testTag: String? = null,
) {
    customItem(
        appbarContent = {
            TooltipBox(
                positionProvider =
                    TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
                tooltip = { PlainTooltip { Text(label()) } },
                state = rememberTooltipState(),
                modifier = if (testTag != null) Modifier.testTag(testTag) else Modifier
            ) {
                IconButton(onClick = onClick, enabled = enabled, content = icon)
            }
        },
        menuContent = { state ->
            DropdownMenuItem(
                leadingIcon = icon,
                enabled = enabled,
                text = { Text(label()) },
                onClick = {
                    onClick()
                    state.dismiss()
                },
                modifier = if (testTag != null) Modifier.testTag(testTag) else Modifier
            )
        }
    )
}

fun AppBarScope.toggleableItem(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    icon: @Composable () -> Unit,
    label: @Composable () -> String,
    enabled: Boolean = true,
) {
    customItem(
        appbarContent = {
            TooltipBox(
                positionProvider =
                    TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
                tooltip = { PlainTooltip { Text(label()) } },
                state = rememberTooltipState(),
            ) {
                IconToggleButton(
                    checked = checked,
                    onCheckedChange = onCheckedChange,
                    enabled = enabled,
                    content = icon,
                )
            }
        },
        menuContent = { state ->
            DropdownMenuItem(
                leadingIcon = icon,
                enabled = enabled,
                text = { Text(label()) },
                onClick = {
                    onCheckedChange(!checked)
                    state.dismiss()
                },
            )
        }
    )
}

fun AppBarRowScope.settingsItem(onClick: () -> Unit) {
    clickableItem(
        onClick = onClick,
        icon = {
            Icon(ComicIcons.Settings, null)
        },
        label = { stringResource(Res.string.label_settings) },
        testTag = "SettingsButton"
    )
}
