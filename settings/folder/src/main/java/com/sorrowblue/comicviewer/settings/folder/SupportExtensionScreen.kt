package com.sorrowblue.comicviewer.settings.folder

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Info
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.sorrowblue.comicviewer.domain.model.SupportExtension
import com.sorrowblue.comicviewer.framework.compose.AppMaterialTheme
import com.sorrowblue.comicviewer.settings.folder.section.SupportExtensionTopAppBar

@Composable
internal fun SupportExtensionRoute(
    onBackClick: () -> Unit,
    viewModel: SupportExtensionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    SupportExtensionScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onExtensionToggle = viewModel::toggleExtension
    )
}

internal data class SupportExtensionScreenUiState(
    val supportExtension: Set<SupportExtension> = emptySet()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SupportExtensionScreen(
    uiState: SupportExtensionScreenUiState = SupportExtensionScreenUiState(),
    onBackClick: () -> Unit = {},
    onExtensionToggle: (SupportExtension) -> Unit = {},
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            SupportExtensionTopAppBar(
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
            Text(
                text = stringResource(R.string.settings_folder_label_archives),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            SupportExtension.Archive.entries.forEach { extension ->
                Box {
                    ListItem(
                        headlineContent = {
                            Text(text = extension.extension)
                        },
                        trailingContent = {
                            Checkbox(
                                checked = uiState.supportExtension.contains(extension),
                                onCheckedChange = {})
                        }
                    )
                    Box(
                        Modifier
                            .matchParentSize()
                            .clickable { onExtensionToggle(extension) })
                }
            }
            Text(
                text = stringResource(R.string.settings_folder_label_document),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            val context = LocalContext.current
            val sim = remember { SplitInstallManagerFactory.create(context) }
            val isDocumentEnabled = sim.installedModules.contains("document")
            if (!isDocumentEnabled) {
                ListItem(
                    headlineContent = {
                        Text(
                            text = "ドキュメントファイルを読むためには、拡張機能をインストールする必要があります。",
                        )
                    },
                    leadingContent = {
                        Icon(Icons.TwoTone.Info, contentDescription = null)
                    }
                )
                FilledTonalButton(
                    onClick = { /*TODO*/ },
                    modifier = Modifier
                        .padding(horizontal = AppMaterialTheme.dimens.margin)
                        .align(Alignment.End)
                ) {
                    Text(text = "拡張機能一覧へ")
                }
            }
            SupportExtension.Document.entries.forEach { extension ->
                Box {
                    ListItem(
                        headlineContent = {
                            Text(text = extension.extension)
                        },
                        trailingContent = {
                            Checkbox(
                                checked = isDocumentEnabled && uiState.supportExtension.contains(
                                    extension
                                ),
                                onCheckedChange = {},
                                enabled = isDocumentEnabled
                            )
                        },
                    )
                    Box(
                        Modifier
                            .matchParentSize()
                            .clickable { onExtensionToggle(extension) })
                }
            }
        }
    }
}

@MultiThemePreviews
@Composable
private fun PreviewSupportExtensionScreen() {
    AppMaterialTheme {
        Surface {
            SupportExtensionScreen()
        }
    }
}
