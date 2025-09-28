package com.sorrowblue.comicviewer.feature.settings.common

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import comicviewer.feature.settings.generated.resources.Res
import comicviewer.feature.settings.generated.resources.settings_label_tutorial
import org.jetbrains.compose.resources.stringResource
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
private fun PreviewSettingsScreen() {
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
                var media by remember { mutableFloatStateOf(0.5f) }
                SliderSetting(
                    title = { Text("メディアの音量") },
                    value = media,
                    onValueChange = { media = it },
                    icon = { Icon(ComicIcons.MusicNote, null) },
                    modifier = Modifier.clickable { },
                )
                SettingsCategory(title = {
                    Text("ディスプレイのロック")
                }) {
                    var checked by remember { mutableStateOf(false) }
                    SwitchSetting(
                        title = {
                            Text("電源ロックの音")
                        },
                        checked = checked,
                        onCheckedChange = { checked = it },
                    )

                    var checked2 by remember { mutableStateOf(false) }
                    SeparateSwitchSetting(title = {
                        Text("電源ロックの音")
                    }, checked = checked2, onCheckedChange = { checked2 = it }, onClick = {})
                }
                ListItem(
                    headlineContent = { Text(stringResource(Res.string.settings_label_tutorial)) },
                    leadingContent = { Icon(ComicIcons.Start, null) },
                    modifier = Modifier.clickable { },
                )
            }
        }
    }
}
