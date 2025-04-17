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
import com.sorrowblue.cmpdestinations.DestinationStyle
import com.sorrowblue.cmpdestinations.annotation.Destination
import com.sorrowblue.cmpdestinations.result.NavResultSender
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
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

@Serializable
data class SortTypeSettings(val sortType: SortType)

@Destination<SortTypeSettings>(style = DestinationStyle.Dialog::class)
@Composable
internal fun SortTypeSettingsScreen(
    route: SortTypeSettings,
    resultNavigator: NavResultSender<SortType>,
) {
    SortTypeSettingsScreen(
        currentFileSort = route.sortType,
        onFileSortChange = resultNavigator::navigateBack,
        onDismissRequest = resultNavigator::navigateBack
    )
}

@Composable
internal fun SortTypeSettingsScreen(
    currentFileSort: SortType,
    onFileSortChange: (SortType) -> Unit,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = stringResource(Res.string.settings_folder_filesort_title)) }
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
        is SortType.Name -> if (isAsc) Res.string.settings_folder_filesort_label_file_sort_name_asc else Res.string.settings_folder_filesort_label_file_sort_name_desc
        is SortType.Date -> if (isAsc) Res.string.settings_folder_filesort_label_file_sort_date_asc else Res.string.settings_folder_filesort_label_file_sort_date_desc
        is SortType.Size -> if (isAsc) Res.string.settings_folder_filesort_label_file_sort_size_asc else Res.string.settings_folder_filesort_label_file_sort_size_desc
    }
