package com.sorrowblue.comicviewer.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.twotone.ArrowBack
import androidx.compose.material.icons.twotone.DisplaySettings
import androidx.compose.material.icons.twotone.FolderOpen
import androidx.compose.material.icons.twotone.Image
import androidx.compose.material.icons.twotone.Info
import androidx.compose.material.icons.twotone.Lock
import androidx.compose.material.icons.twotone.Start
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.framework.compose.AppMaterialTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = { /* TODO() */ }) {
                        Icon(imageVector = Icons.TwoTone.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
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
                modifier = Modifier.clickable { },
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
                modifier = Modifier.clickable { },
            )
            ListItem(
                headlineContent = { Text(stringResource(R.string.settings_label_tutorial)) },
                leadingContent = { Icon(Icons.TwoTone.Start, null) },
                modifier = Modifier.clickable { },
            )


            ListItem(
                headlineContent = { Text(stringResource(R.string.settings_label_display)) },
                leadingContent = { Icon(Icons.TwoTone.DisplaySettings, null) },
                modifier = Modifier.clickable { },
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
                modifier = Modifier.clickable { },
            )
            ListItem(
                headlineContent = { Text(stringResource(R.string.settings_label_tutorial)) },
                leadingContent = { Icon(Icons.TwoTone.Start, null) },
                modifier = Modifier.clickable { },
            )


            ListItem(
                headlineContent = { Text(stringResource(R.string.settings_label_display)) },
                leadingContent = { Icon(Icons.TwoTone.DisplaySettings, null) },
                modifier = Modifier.clickable { },
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
                modifier = Modifier.clickable { },
            )
            ListItem(
                headlineContent = { Text(stringResource(R.string.settings_label_tutorial)) },
                leadingContent = { Icon(Icons.TwoTone.Start, null) },
                modifier = Modifier.clickable { },
            )


        }
    }
}

data class SettingsItem(
    val title: Int,
    val icon: ImageVector? = null
)

@Preview
@Composable
fun PreviewSettingsScreen() {
    AppMaterialTheme {
        SettingsScreen()
    }
}

@Preview
@Composable
fun PreviewSample() {
    AppMaterialTheme {
        Column {
            ListItem(
                headlineContent = { Text("One line list item with 24x24 icon") },
                modifier = Modifier.clickable { },
            )
            Text(
                text = "最近のアクセス",
                Modifier.padding(start = 16.dp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
            ListItem(
                headlineContent = { Text("One line list item with 24x24 icon") },
                supportingContent = { Text("One line list item with 24x24 icon") },
                modifier = Modifier.clickable { },
            )
            ListItem(
                headlineContent = { Text("One line list item with 24x24 icon") },
                supportingContent = { Text("One line list item with 24x24 icon") },
                modifier = Modifier.clickable { },
                leadingContent = {
                    Icon(
                        Icons.Filled.Favorite,
                        contentDescription = "Localized description",
                    )
                }
            )
            var checked by remember { mutableStateOf(false) }
            ListItem(
                headlineContent = { Text("One line list item with 24x24 icon") },
                supportingContent = { Text("One line list item with 24x24 icon") },
                modifier = Modifier.clickable { checked = !checked },
                trailingContent = {
                    Switch(checked = checked, onCheckedChange = { checked = !checked })
                }
            )
        }
    }
}

