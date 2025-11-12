package com.sorrowblue.comicviewer.feature.settings.folder

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderThumbnailOrder
import com.sorrowblue.comicviewer.framework.ui.material3.AlertDialog
import comicviewer.feature.settings.folder.generated.resources.Res
import comicviewer.feature.settings.folder.generated.resources.settings_folder_folderthumbnail_label_last_read
import comicviewer.feature.settings.folder.generated.resources.settings_folder_folderthumbnail_label_modified
import comicviewer.feature.settings.folder.generated.resources.settings_folder_folderthumbnail_label_name
import comicviewer.feature.settings.folder.generated.resources.settings_folder_folderthumbnail_title
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun FolderThumbnailOrderScreen(
    currentFolderThumbnailOrder: FolderThumbnailOrder,
    onFolderThumbnailOrderChange: (FolderThumbnailOrder) -> Unit,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = stringResource(Res.string.settings_folder_folderthumbnail_title)) },
    ) {
        Column {
            FolderThumbnailOrder.entries.forEach { order ->
                ListItem(
                    modifier = Modifier
                        .clickable { onFolderThumbnailOrderChange(order) },
                    leadingContent = {
                        RadioButton(selected = order == currentFolderThumbnailOrder, onClick = null)
                    },
                    headlineContent = {
                        Text(text = stringResource(order.displayText))
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                )
            }
        }
    }
}

internal val FolderThumbnailOrder.displayText
    get() = when (this) {
        FolderThumbnailOrder.NAME -> Res.string.settings_folder_folderthumbnail_label_name
        FolderThumbnailOrder.MODIFIED -> Res.string.settings_folder_folderthumbnail_label_modified
        FolderThumbnailOrder.LAST_READ -> Res.string.settings_folder_folderthumbnail_label_last_read
    }
