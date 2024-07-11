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
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.feature.settings.folder.R
import com.sorrowblue.comicviewer.feature.settings.folder.navigation.FolderSettingsGraph
import com.sorrowblue.comicviewer.framework.ui.material3.AlertDialog
import com.sorrowblue.comicviewer.framework.ui.material3.PreviewTheme

@Destination<FolderSettingsGraph>(
    style = DestinationStyle.Dialog::class,
    visibility = CodeGenVisibility.INTERNAL
)
@Composable
internal fun FileSortDialog(
    fileSort: SortType,
    resultNavigator: ResultBackNavigator<SortType>,
) {
    FileSortDialog(
        currentFileSort = fileSort,
        onFileSortChange = resultNavigator::navigateBack,
        onDismissRequest = resultNavigator::navigateBack
    )
}

@Composable
private fun FileSortDialog(
    currentFileSort: SortType,
    onFileSortChange: (SortType) -> Unit,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = stringResource(R.string.settings_folder_filesort_title)) }
    ) {
        Column {
            SortType.entries.forEach { fileSort ->
                ListItem(
                    modifier = Modifier
                        .clickable { onFileSortChange(fileSort) },
                    leadingContent = {
                        RadioButton(selected = fileSort == currentFileSort, onClick = null)
                    },
                    headlineContent = {
                        Text(text = stringResource(fileSort.displayText))
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )
            }
        }
    }
}

internal val SortType.displayText
    get() = when (this) {
        is SortType.NAME -> if (isAsc) R.string.settings_folder_filesort_label_file_sort_name_asc else R.string.settings_folder_filesort_label_file_sort_name_desc
        is SortType.DATE -> if (isAsc) R.string.settings_folder_filesort_label_file_sort_date_asc else R.string.settings_folder_filesort_label_file_sort_date_desc
        is SortType.SIZE -> if (isAsc) R.string.settings_folder_filesort_label_file_sort_size_asc else R.string.settings_folder_filesort_label_file_sort_size_desc
    }

@Composable
@Preview
private fun FileSortDialogPreview() {
    PreviewTheme {
        var imageFormat by remember { mutableStateOf<SortType>(SortType.NAME(true)) }
        FileSortDialog(
            currentFileSort = imageFormat,
            onFileSortChange = { imageFormat = it },
            onDismissRequest = {}
        )
    }
}
