package com.sorrowblue.comicviewer.feature.settings.folder

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sorrowblue.comicviewer.feature.settings.folder.section.SettingsFolderTopAppBar
import com.sorrowblue.comicviewer.framework.compose.AppMaterialTheme

internal data class SettingsFolderScreenUiState(
    val isOpenImageFolder: Boolean = false,
    val isThumbnailEnabled: Boolean = false
)

@Composable
internal fun SettingsFolderRoute(
    onBackClick: () -> Unit,
    onExtensionClick: () -> Unit,
    viewModel: SettingsFolderViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    SettingsFolderScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onExtensionClick = onExtensionClick,
        onChangeOpenImageFolder = viewModel::updateResolveImageFolder,
        onChangeThumbnailEnabled = viewModel::updateShowPreview,
        onDeleteThumbnailClick = viewModel::deleteThumbnail,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsFolderScreen(
    uiState: SettingsFolderScreenUiState = SettingsFolderScreenUiState(),
    onBackClick: () -> Unit = {},
    onExtensionClick: () -> Unit = {},
    onChangeOpenImageFolder: (Boolean) -> Unit = {},
    onChangeThumbnailEnabled: (Boolean) -> Unit = {},
    onDeleteThumbnailClick: () -> Unit = {},
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            SettingsFolderTopAppBar(
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
                headlineContent = {
                    Text(text = "拡張子設定")
                },
                modifier = Modifier.clickable(onClick = onExtensionClick),
            )
            ListItem(
                headlineContent = {
                    Text(text = "画像フォルダを本として扱う")
                },
                supportingContent = {
                    Text(text = "一覧の取得に時間がかかります")
                },
                trailingContent = {
                    Switch(
                        checked = uiState.isOpenImageFolder,
                        onCheckedChange = onChangeOpenImageFolder
                    )
                },
                modifier = Modifier.clickable { }
            )
            Text(
                "サムネイル",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            ListItem(
                headlineContent = {
                    Text(text = "サムネイルを表示する")
                },
                trailingContent = {
                    Switch(
                        checked = uiState.isThumbnailEnabled,
                        onCheckedChange = onChangeThumbnailEnabled
                    )
                },
                modifier = Modifier.clickable { },
            )
            ListItem(
                headlineContent = {
                    Text(text = "サムネイルを削除")
                },
                modifier = Modifier.clickable(onClick = onDeleteThumbnailClick),
            )
        }
    }
}

@MultiThemePreviews
@Composable
private fun PreviewSettingsFolderScreen() {
    AppMaterialTheme {
        Surface {
            SettingsFolderScreen()
        }
    }
}

@Preview(name = "Light Mode", uiMode = Configuration.UI_MODE_NIGHT_NO,
    device = "spec:width=673dp,height=841dp"
)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
annotation class MultiThemePreviews
