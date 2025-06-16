package com.sorrowblue.comicviewer.folder

import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.sorrowblue.cmpdestinations.DestinationStyle
import com.sorrowblue.cmpdestinations.annotation.Destination
import com.sorrowblue.cmpdestinations.result.NavResultSender
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import comicviewer.feature.folder.generated.resources.Res
import comicviewer.feature.folder.generated.resources.folder_sorttype_label_date_asc
import comicviewer.feature.folder.generated.resources.folder_sorttype_label_date_desc
import comicviewer.feature.folder.generated.resources.folder_sorttype_label_name_asc
import comicviewer.feature.folder.generated.resources.folder_sorttype_label_name_desc
import comicviewer.feature.folder.generated.resources.folder_sorttype_label_size_asc
import comicviewer.feature.folder.generated.resources.folder_sorttype_label_size_desc
import comicviewer.feature.folder.generated.resources.folder_sorttype_title_sort_by
import kotlinx.serialization.Serializable
import logcat.logcat
import org.jetbrains.compose.resources.stringResource

@Serializable
data class SortTypeSelect(val sortType: SortType, val folderScopeOnly: Boolean = false)

@Destination<SortTypeSelect>(style = DestinationStyle.Dialog::class)
@Composable
internal fun SortTypeSelectScreen(
    route: SortTypeSelect,
    resultNavigator: NavResultSender<SortTypeSelect>,
) {
    var currentFolderScopeOnly by remember(route.folderScopeOnly) { mutableStateOf(route.folderScopeOnly) }
    logcat("SortTypeSelectScreen") { "currentFolderScopeOnly: $currentFolderScopeOnly" }
    SortTypeSelectScreen(
        currentSortType = route.sortType,
        folderScopeOnly = currentFolderScopeOnly,
        onFolderScopeOnlyClick = { currentFolderScopeOnly = !currentFolderScopeOnly },
        onDismissRequest = {
            resultNavigator.navigateBack(
                SortTypeSelect(
                    route.sortType,
                    currentFolderScopeOnly
                )
            )
        },
        onClick = { resultNavigator.navigateBack(SortTypeSelect(it, currentFolderScopeOnly)) }
    )
}

@Composable
private fun SortTypeSelectScreen(
    currentSortType: SortType,
    folderScopeOnly: Boolean,
    onDismissRequest: () -> Unit,
    onClick: (SortType) -> Unit,
    onFolderScopeOnlyClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        contentWindowInsets = { WindowInsets(0) }
    ) {
        Text(
            text = stringResource(Res.string.folder_sorttype_title_sort_by),
            style = ComicTheme.typography.titleLarge,
            modifier = Modifier
                .padding(horizontal = ComicTheme.dimension.margin)
                .padding(bottom = ComicTheme.dimension.padding)
        )
        HorizontalDivider()
        LazyColumn {
            item {
                Column {
                    ListItem(
                        headlineContent = {
                            Text(text = "このフォルダにだけ適用")
                        },
                        leadingContent = {
                            Checkbox(
                                checked = folderScopeOnly,
                                onCheckedChange = { onFolderScopeOnlyClick() },
                                modifier = Modifier.focusable(false)
                            )
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        modifier = Modifier.clickable { onFolderScopeOnlyClick() }
                    )
                    HorizontalDivider()
                }
            }
            items(SortType.entries) { item ->
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(item.displayText()),
                        )
                    },
                    trailingContent = {
                        if (item == currentSortType) {
                            Icon(ComicIcons.Check, contentDescription = null)
                        }
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    modifier = Modifier.clickable { onClick(item) }
                )
            }
        }
        Spacer(modifier = Modifier.navigationBarsPadding())
    }
}

private fun SortType.displayText() = when (this) {
    is SortType.Date -> if (isAsc) Res.string.folder_sorttype_label_date_asc else Res.string.folder_sorttype_label_date_desc
    is SortType.Name -> if (isAsc) Res.string.folder_sorttype_label_name_asc else Res.string.folder_sorttype_label_name_desc
    is SortType.Size -> if (isAsc) Res.string.folder_sorttype_label_size_asc else Res.string.folder_sorttype_label_size_desc
}
