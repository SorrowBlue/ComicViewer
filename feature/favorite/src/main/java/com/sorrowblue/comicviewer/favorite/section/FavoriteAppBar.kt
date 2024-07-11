package com.sorrowblue.comicviewer.favorite.section

import android.os.Parcelable
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import com.sorrowblue.comicviewer.domain.model.settings.folder.FileListDisplay
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderDisplaySettingsDefaults
import com.sorrowblue.comicviewer.domain.model.settings.folder.GridColumnSize
import com.sorrowblue.comicviewer.feature.favorite.R
import com.sorrowblue.comicviewer.file.component.ChangeGridSize
import com.sorrowblue.comicviewer.file.component.FileContentType
import com.sorrowblue.comicviewer.file.component.rememberFileContentType
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.material3.OverflowMenu
import com.sorrowblue.comicviewer.framework.ui.material3.OverflowMenuItem
import com.sorrowblue.comicviewer.framework.ui.material3.PlainTooltipBox
import com.sorrowblue.comicviewer.framework.ui.material3.rememberOverflowMenuState
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class FavoriteAppBarUiState(
    val title: String = "",
    val fileListDisplay: FileListDisplay = FolderDisplaySettingsDefaults.fileListDisplay,
    val gridColumnSize: GridColumnSize = FolderDisplaySettingsDefaults.gridColumnSize,
) : Parcelable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FavoriteAppBar(
    uiState: FavoriteAppBarUiState,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onFileListChange: () -> Unit,
    onGridSizeChange: () -> Unit,
    onDeleteClick: () -> Unit,
    onSettingsClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    TopAppBar(
        title = { Text(text = uiState.title) },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(imageVector = ComicIcons.ArrowBack, contentDescription = "Back")
            }
        },
        actions = {
            PlainTooltipBox(tooltipContent = { Text(stringResource(R.string.favorite_title_edit)) }) {
                IconButton(onClick = onEditClick) {
                    Icon(ComicIcons.Edit, stringResource(R.string.favorite_title_edit))
                }
            }

            val fileContentType by rememberFileContentType(
                fileListDisplay = uiState.fileListDisplay,
                gridColumnSize = uiState.gridColumnSize
            )
            OverflowMenu(state = rememberOverflowMenuState()) {
                FileContentType(fileContentType = fileContentType, onClick = onFileListChange)
                ChangeGridSize(fileContentType = fileContentType, onClick = onGridSizeChange)
                OverflowMenuItem(
                    text = stringResource(R.string.favorite_action_delete),
                    icon = ComicIcons.Delete,
                    onClick = onDeleteClick
                )
                OverflowMenuItem(
                    text = stringResource(
                        id = com.sorrowblue.comicviewer.framework.ui.R.string.framework_ui_label_settings
                    ),
                    icon = ComicIcons.Settings,
                    onClick = onSettingsClick
                )
            }
        },
        windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top),
        scrollBehavior = scrollBehavior
    )
}
