package com.sorrowblue.comicviewer.feature.settings.folder.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.parameters.CodeGenVisibility
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderDisplaySettingsDefaults
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderThumbnailOrder
import com.sorrowblue.comicviewer.feature.settings.folder.R
import com.sorrowblue.comicviewer.feature.settings.folder.navigation.FolderSettingsGraph
import com.sorrowblue.comicviewer.framework.ui.material3.AlertDialog
import com.sorrowblue.comicviewer.framework.ui.material3.PreviewTheme

@Destination<FolderSettingsGraph>(
    style = DestinationStyle.Dialog::class,
    visibility = CodeGenVisibility.INTERNAL
)
@Composable
internal fun FolderThumbnailOrderDialog(
    folderThumbnailOrder: FolderThumbnailOrder,
    resultNavigator: ResultBackNavigator<FolderThumbnailOrder>,
) {
    FolderThumbnailOrderDialog(
        currentFolderThumbnailOrder = folderThumbnailOrder,
        onFolderThumbnailOrderChange = resultNavigator::navigateBack,
        onDismissRequest = resultNavigator::navigateBack
    )
}

@Composable
private fun FolderThumbnailOrderDialog(
    currentFolderThumbnailOrder: FolderThumbnailOrder,
    onFolderThumbnailOrderChange: (FolderThumbnailOrder) -> Unit,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = stringResource(R.string.settings_folder_folderthumbnail_title)) }
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
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )
            }
        }
    }
}

internal val FolderThumbnailOrder.displayText
    get() = when (this) {
        FolderThumbnailOrder.NAME -> R.string.settings_folder_folderthumbnail_label_name
        FolderThumbnailOrder.MODIFIED -> R.string.settings_folder_folderthumbnail_label_modified
        FolderThumbnailOrder.LAST_READ -> R.string.settings_folder_folderthumbnail_label_last_read
    }

@Composable
@Preview
private fun FolderThumbnailOrderDialogPreview() {
    PreviewTheme {
        var folderThumbnailOrder by remember { mutableStateOf(FolderDisplaySettingsDefaults.folderThumbnailOrder) }
        FolderThumbnailOrderDialog(
            currentFolderThumbnailOrder = folderThumbnailOrder,
            onFolderThumbnailOrderChange = { folderThumbnailOrder = it },
            onDismissRequest = {}
        )
    }
}
