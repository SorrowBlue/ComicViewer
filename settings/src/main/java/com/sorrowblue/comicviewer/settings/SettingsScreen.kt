package com.sorrowblue.comicviewer.settings

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.DisplaySettings
import androidx.compose.material.icons.twotone.FolderOpen
import androidx.compose.material.icons.twotone.Image
import androidx.compose.material.icons.twotone.Info
import androidx.compose.material.icons.twotone.Lock
import androidx.compose.material.icons.twotone.MusicNote
import androidx.compose.material.icons.twotone.Start
import androidx.compose.material.icons.twotone.VolumeUp
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.framework.compose.AppMaterialTheme
import com.sorrowblue.comicviewer.settings.section.SettingsTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit = {},
    onDisplayClick: () -> Unit = {},
    onAppInfoClick: () -> Unit = {},
    onAppLanguageClick: () -> Unit = {},
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            SettingsTopAppBar(
                onBackClick = onBackClick,
                scrollBehavior = scrollBehavior
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { contentPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(contentPadding)
        ) {
            ListItem(
                headlineContent = { Text(stringResource(R.string.settings_label_display)) },
                leadingContent = { Icon(Icons.TwoTone.DisplaySettings, null) },
                modifier = Modifier.clickable(onClick = onDisplayClick),
            )
            ListItem(
                headlineContent = { Text(stringResource(R.string.settings_label_folder)) },
                leadingContent = { Icon(Icons.TwoTone.FolderOpen, null) },
                modifier = Modifier.clickable { },
            )
            ListItem(
                headlineContent = { Text(stringResource(R.string.settings_label_viewer)) },
                leadingContent = { Icon(Icons.TwoTone.Image, null) },
                modifier = Modifier.clickable { },
            )
            ListItem(
                headlineContent = { Text(stringResource(R.string.settings_label_security)) },
                leadingContent = { Icon(Icons.TwoTone.Lock, null) },
                modifier = Modifier.clickable { },
            )
            ListItem(
                headlineContent = { Text(stringResource(R.string.settings_label_app)) },
                leadingContent = { Icon(Icons.TwoTone.Info, null) },
                modifier = Modifier.clickable {
                    onAppInfoClick()
                },
            )
            ListItem(
                headlineContent = { Text(stringResource(R.string.settings_label_tutorial)) },
                leadingContent = { Icon(Icons.TwoTone.Start, null) },
                modifier = Modifier.clickable { },
            )
            val context = LocalContext.current
            ListItem(
                headlineContent = { Text("言語設定") },
                modifier = Modifier.clickable {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        context.startActivity(
                            Intent(
                                Settings.ACTION_APP_LOCALE_SETTINGS,
                                Uri.parse("package:${context.applicationInfo.packageName}")
                            )
                        )
                    } else {
                        onAppLanguageClick()
                    }
                }
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
fun PreviewSettingsScreen() {
    AppMaterialTheme {
        Surface {
            Column {
                ListItem(
                    headlineContent = { Text("音とバイブレーション") },
                    supportingContent = { Text("音量、ハプティクス、サイレント　モード") },
                    leadingContent = { Icon(Icons.TwoTone.VolumeUp, null) },
                    modifier = Modifier.clickable { },
                )
                var media by remember { mutableFloatStateOf(0f) }
                ListItem(
                    headlineContent = { Text("メディアの音量") },
                    supportingContent = {
                        Slider(value = media, onValueChange = { media = it }, steps = 8)
                    },
                    leadingContent = { Icon(Icons.TwoTone.MusicNote, null) },
                    modifier = Modifier.clickable { },
                )
                var checked by remember { mutableStateOf(false) }
                Text ("ディスプレイのロック", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
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
                            Text("ワイアレスデバッグ")
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
                    leadingContent = { Icon(Icons.TwoTone.Start, null) },
                    modifier = Modifier.clickable { },
                )
            }
        }
    }
}

@Composable
fun VerticalDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = DividerDefaults.Thickness,
    color: Color = DividerDefaults.color,
) {
    val targetThickness = if (thickness == Dp.Hairline) {
        (1f / LocalDensity.current.density).dp
    } else {
        thickness
    }
    Box(
        modifier
            .fillMaxHeight()
            .width(targetThickness)
            .background(color = color)
    )
}
