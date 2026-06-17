package com.sorrowblue.comicviewer.feature.settings.folder.subscreen.sortorder

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.github.skydoves.navgraph.annotations.NavDestination
import com.github.skydoves.navgraph.annotations.NavPreview
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.feature.settings.folder.navigation.SortTypeNavKey
import com.sorrowblue.comicviewer.framework.ui.material3.AlertDialog
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import comicviewer.feature.settings.folder.generated.resources.Res
import comicviewer.feature.settings.folder.generated.resources.settings_folder_sort_order_label_date_asc
import comicviewer.feature.settings.folder.generated.resources.settings_folder_sort_order_label_date_desc
import comicviewer.feature.settings.folder.generated.resources.settings_folder_sort_order_label_name_asc
import comicviewer.feature.settings.folder.generated.resources.settings_folder_sort_order_label_name_desc
import comicviewer.feature.settings.folder.generated.resources.settings_folder_sort_order_label_size_asc
import comicviewer.feature.settings.folder.generated.resources.settings_folder_sort_order_label_size_desc
import comicviewer.feature.settings.folder.generated.resources.settings_folder_sort_order_title
import org.jetbrains.compose.resources.stringResource

@NavDestination(SortTypeNavKey::class)
@Composable
internal fun SortOrderScreen(
    currentSortType: SortType,
    onFileSortChange: (SortType) -> Unit,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = stringResource(Res.string.settings_folder_sort_order_title)) },
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
                        Text(text = sortType.displayText)
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                )
            }
        }
    }
}

internal val SortType.displayText
    @Composable
    get() = when (this) {
        is SortType.Name -> if (isAsc) Res.string.settings_folder_sort_order_label_name_asc else Res.string.settings_folder_sort_order_label_name_desc
        is SortType.Date -> if (isAsc) Res.string.settings_folder_sort_order_label_date_asc else Res.string.settings_folder_sort_order_label_date_desc
        is SortType.Size -> if (isAsc) Res.string.settings_folder_sort_order_label_size_asc else Res.string.settings_folder_sort_order_label_size_desc
    }.let {
        stringResource(it)
    }

@NavPreview(SortTypeNavKey::class, primary = true)
@Preview
@Composable
internal fun SortOrderScreenPreview() = PreviewTheme {
    SortOrderScreen(
        currentSortType = SortType.Name(isAsc = true),
        onFileSortChange = {},
        onDismissRequest = {},
    )
}
