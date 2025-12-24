package com.sorrowblue.comicviewer.feature.collection.section

import androidx.compose.material3.AppBarRow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.file.component.fileListDisplayItem
import com.sorrowblue.comicviewer.file.component.gridSizeItem
import com.sorrowblue.comicviewer.file.component.rememberFileListDisplayItemState
import com.sorrowblue.comicviewer.file.component.rememberGridSizeItemState
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.adaptive.AdaptiveAppBar
import com.sorrowblue.comicviewer.framework.ui.material3.BackIconButton
import com.sorrowblue.comicviewer.framework.ui.material3.clickableItem
import com.sorrowblue.comicviewer.framework.ui.material3.settingsItem
import comicviewer.feature.collection.generated.resources.Res
import comicviewer.feature.collection.generated.resources.collection_label_delete
import comicviewer.feature.collection.generated.resources.collection_label_edit
import org.jetbrains.compose.resources.stringResource

internal data class CollectionAppBarUiState(val title: String = "")

@Composable
internal fun CollectionAppBar(
    uiState: CollectionAppBarUiState,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    AdaptiveAppBar(
        title = { Text(text = uiState.title) },
        navigationIcon = { BackIconButton(onClick = onBackClick) },
        actions = {
            val gridSizeItemState = rememberGridSizeItemState()
            val fileListDisplayItemState = rememberFileListDisplayItemState()
            AppBarRow(maxItemCount = 3) {
                clickableItem(
                    icon = { Icon(ComicIcons.Edit, null) },
                    label = { stringResource(Res.string.collection_label_edit) },
                    onClick = onEditClick,
                    testTag = "EditButton",
                )
                clickableItem(
                    icon = { Icon(ComicIcons.Delete, null) },
                    label = { stringResource(Res.string.collection_label_delete) },
                    onClick = onDeleteClick,
                    testTag = "DeleteButton",
                )
                fileListDisplayItemState.fileListDisplayItem()
                gridSizeItemState.gridSizeItem()
                settingsItem(onClick = onSettingsClick)
            }
        },
    )
}
