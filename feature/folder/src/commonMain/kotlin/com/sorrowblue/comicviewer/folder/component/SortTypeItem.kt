package com.sorrowblue.comicviewer.folder.component

import androidx.compose.foundation.focusable
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.SplitButtonDefaults
import androidx.compose.material3.SplitButtonLayout
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import comicviewer.feature.folder.generated.resources.Res
import comicviewer.feature.folder.generated.resources.folder_label_file_size
import comicviewer.feature.folder.generated.resources.folder_label_name
import comicviewer.feature.folder.generated.resources.folder_label_update_date
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun SortTypeItem(
    sortType: SortType,
    folderScopeOnly: Boolean,
    onFolderScopeOnlyClick: () -> Unit,
    onClick: (SortType) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    SplitButtonLayout(
        leadingButton = {
            SplitButtonDefaults.LeadingButton(
                onClick = { expanded = true },
                colors = ButtonDefaults.textButtonColors(),
            ) {
                Text(text = stringResource(sortType.displayText()))
            }
        },
        trailingButton = {
            SplitButtonDefaults.TrailingButton(
                checked = true,
                onCheckedChange = {
                    expanded = true
                },
            ) {
                Icon(if (sortType.isAsc) ComicIcons.ArrowUpward else ComicIcons.ArrowDownward, null)
            }
        },
    )
    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
        DropdownMenuItem(
            text = {
                Text(text = "このフォルダにだけ適用")
            },
            leadingIcon = {
                Checkbox(
                    checked = folderScopeOnly,
                    onCheckedChange = { onFolderScopeOnlyClick() },
                    modifier = Modifier.focusable(false),
                )
            },
            onClick = {
                onFolderScopeOnlyClick()
            },
        )
        HorizontalDivider()
        SortType.entries.forEach { item ->
            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(item.displayText()),
                    )
                },
                leadingIcon = {
                    if (item == sortType) {
                        Icon(ComicIcons.Check, contentDescription = null)
                    }
                },
                onClick = {
                    onClick(item)
                    expanded = false
                },
            )
        }
    }
}

private fun SortType.displayText() = when (this) {
    is SortType.Date -> Res.string.folder_label_update_date
    is SortType.Name -> Res.string.folder_label_name
    is SortType.Size -> Res.string.folder_label_file_size
}
