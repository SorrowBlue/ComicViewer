package com.sorrowblue.comicviewer.framework.ui.material3

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme

@Composable
fun SwitchWithText(
    text: @Composable () -> Unit,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    thumbContent: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    colors: SwitchColors = SwitchDefaults.colors(),
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        CompositionLocalProvider(LocalTextStyle provides ComicTheme.typography.bodyMedium) {
            Box(modifier = Modifier.alpha(if (enabled) EnableAlpha else DisableAlpha)) {
                text()
            }
        }
        Spacer(Modifier.weight(1f))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            thumbContent = thumbContent,
            colors = colors,
        )
    }
}

private const val DisableAlpha = 0.38f
private const val EnableAlpha = 1f
