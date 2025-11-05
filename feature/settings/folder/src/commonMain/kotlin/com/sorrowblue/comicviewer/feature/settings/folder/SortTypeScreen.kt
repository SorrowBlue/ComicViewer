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
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.framework.ui.material3.AlertDialog
import comicviewer.feature.settings.folder.generated.resources.Res
import comicviewer.feature.settings.folder.generated.resources.settings_folder_filesort_label_file_sort_date_asc
import comicviewer.feature.settings.folder.generated.resources.settings_folder_filesort_label_file_sort_date_desc
import comicviewer.feature.settings.folder.generated.resources.settings_folder_filesort_label_file_sort_name_asc
import comicviewer.feature.settings.folder.generated.resources.settings_folder_filesort_label_file_sort_name_desc
import comicviewer.feature.settings.folder.generated.resources.settings_folder_filesort_label_file_sort_size_asc
import comicviewer.feature.settings.folder.generated.resources.settings_folder_filesort_label_file_sort_size_desc
import comicviewer.feature.settings.folder.generated.resources.settings_folder_filesort_title
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun SortTypeScreen(
    currentSortType: SortType,
    onFileSortChange: (SortType) -> Unit,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = stringResource(Res.string.settings_folder_filesort_title)) }
    ) {
        Column {
            SortType.entries.forEach { sortType ->
                ListItem(
                    modifier = Modifier
                        .clickable { onFileSortChange(sortType) },
                    leadingContent = {
                        RadioButton(selected = currentSortType == sortType, onClick = null)
                    },
                    headlineContent = {
                        Text(text = stringResource(sortType.displayText))
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )
            }
        }
    }
}

internal val SortType.displayText
    get() = when (this) {
        is SortType.Name -> if (isAsc) Res.string.settings_folder_filesort_label_file_sort_name_asc else Res.string.settings_folder_filesort_label_file_sort_name_desc
        is SortType.Date -> if (isAsc) Res.string.settings_folder_filesort_label_file_sort_date_asc else Res.string.settings_folder_filesort_label_file_sort_date_desc
        is SortType.Size -> if (isAsc) Res.string.settings_folder_filesort_label_file_sort_size_asc else Res.string.settings_folder_filesort_label_file_sort_size_desc
    }
