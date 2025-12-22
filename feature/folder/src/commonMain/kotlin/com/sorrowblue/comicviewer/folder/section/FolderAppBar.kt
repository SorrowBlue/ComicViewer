package com.sorrowblue.comicviewer.folder.section

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AppBarRow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.file.component.FileListDisplayItem
import com.sorrowblue.comicviewer.file.component.gridSizeItem
import com.sorrowblue.comicviewer.file.component.hiddenFilesToggleableItem
import com.sorrowblue.comicviewer.file.component.rememberGridSizeItemState
import com.sorrowblue.comicviewer.file.component.rememberHiddenFilesToggleableItemState
import com.sorrowblue.comicviewer.folder.component.SortTypeItem
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.LocalSharedTransitionScope
import com.sorrowblue.comicviewer.framework.ui.adaptive.AdaptiveAppBar
import com.sorrowblue.comicviewer.framework.ui.material3.BackIconButton
import com.sorrowblue.comicviewer.framework.ui.material3.clickableItem
import com.sorrowblue.comicviewer.framework.ui.material3.settingsItem
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import comicviewer.feature.folder.generated.resources.Res
import comicviewer.feature.folder.generated.resources.folder_label_search
import org.jetbrains.compose.resources.stringResource

internal data class FolderAppBarUiState(
    val title: String = "",
    val folderScopeOnly: Boolean = false,
    val sortType: SortType = SortType.Name(true),
    val showSearch: Boolean = false,
)

@Composable
internal fun FolderAppBar(
    uiState: FolderAppBarUiState,
    onBackClick: () -> Unit,
    onSearchClick: () -> Unit,
    onSortClick: (SortType) -> Unit,
    onFolderScopeOnlyClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    with(LocalSharedTransitionScope.current) {
        AdaptiveAppBar(
            title = { Text(text = uiState.title) },
            navigationIcon = {
                BackIconButton(onClick = onBackClick)
            },
            actions = {
                val gridSizeItemState = rememberGridSizeItemState()
                val hiddenFilesToggleableItemState = rememberHiddenFilesToggleableItemState()
                AppBarRow(maxItemCount = 2, modifier = Modifier.testTag("AppBarMenu")) {
                    clickableItem(
                        onClick = onSearchClick,
                        icon = {
                            Icon(
                                ComicIcons.Search,
                                stringResource(Res.string.folder_label_search),
                            )
                        },
                        label = { stringResource(Res.string.folder_label_search) }
                    )
                    gridSizeItemState.gridSizeItem()
                    hiddenFilesToggleableItemState.hiddenFilesToggleableItem()
                    settingsItem(onClick = onSettingsClick)
                }
            },
            bottomComponent = {
                Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                    SortTypeItem(
                        sortType = uiState.sortType,
                        folderScopeOnly = uiState.folderScopeOnly,
                        onFolderScopeOnlyClick = {
                            onFolderScopeOnlyClick()
                        },
                        onClick = {
                            onSortClick(it)
                        },
                    )
                    Spacer(Modifier.weight(1f))
                    FileListDisplayItem()
                }
            },
            scrollBehavior = scrollBehavior,
            modifier = modifier,
        )
    }
}

@Composable
@Preview
private fun FolderAppBarPreview() {
    PreviewTheme {
        FolderAppBar(
            uiState = FolderAppBarUiState(
                title = "Folder Name",
                sortType = SortType.Name(true),
                folderScopeOnly = true,
            ),
            onBackClick = {},
            onSearchClick = {},
            onSortClick = {},
            onFolderScopeOnlyClick = {},
            onSettingsClick = {},
        )
    }
}
