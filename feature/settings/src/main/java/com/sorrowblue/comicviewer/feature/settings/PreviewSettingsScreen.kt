package com.sorrowblue.comicviewer.feature.settings

import android.content.res.Configuration
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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.material3.PreviewTheme

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
fun PreviewSettingsScreen() {
    PreviewTheme {
        Surface {
            Column {
                ListItem(
                    headlineContent = { Text("音とバイブレーション") },
                    supportingContent = { Text("音量、ハプティクス、サイレント　モード") },
                    leadingContent = { Icon(ComicIcons.VolumeUp, null) },
                    modifier = Modifier.clickable { },
                )
                var media by remember { mutableFloatStateOf(0f) }
                ListItem(
                    headlineContent = { Text("メディアの音量") },
                    supportingContent = {
                        Slider(value = media, onValueChange = { media = it }, steps = 8)
                    },
                    leadingContent = { Icon(ComicIcons.MusicNote, null) },
                    modifier = Modifier.clickable { },
                )
                var checked by remember { mutableStateOf(false) }
                Text(
                    "ディスプレイのロック",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                Box {
                    ListItem(
                        headlineContent = { Text("電源ロックの音") },
                        trailingContent = {
                            Switch(checked = checked, onCheckedChange = { checked = !checked })
                        },
                    )
                    Box(
                        Modifier
                            .matchParentSize()
                            .clickable { checked = !checked })
                }

                var checked2 by remember { mutableStateOf(false) }
                ListItem(
                    modifier = Modifier.clickable { },
                    headlineContent = {
                        Row(Modifier.height(IntrinsicSize.Max)) {
                            Text("ワイヤレスデバッグ")
                            Spacer(Modifier.weight(1f))
                            VerticalDivider()
                        }
                    },
                    trailingContent = {
                        Switch(checked = checked2, onCheckedChange = { checked2 = !checked2 })
                    },
                )
                ListItem(
                    headlineContent = { Text(stringResource(R.string.settings_label_tutorial)) },
                    leadingContent = { Icon(ComicIcons.Start, null) },
                    modifier = Modifier.clickable { },
                )
            }
        }
    }
}
