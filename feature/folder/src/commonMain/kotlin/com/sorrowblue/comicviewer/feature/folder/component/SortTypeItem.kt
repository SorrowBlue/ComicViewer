/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.folder.component

import androidx.compose.foundation.focusable
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SplitButtonDefaults
import androidx.compose.material3.SplitButtonLayout
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderSortScope
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import comicviewer.feature.folder.generated.resources.Res
import comicviewer.feature.folder.generated.resources.folder_label_file_size
import comicviewer.feature.folder.generated.resources.folder_label_name
import comicviewer.feature.folder.generated.resources.folder_label_update_date
import comicviewer.feature.folder.generated.resources.folder_sorttype_label_apply_to_all
import comicviewer.feature.folder.generated.resources.folder_sorttype_label_apply_to_folder_only
import comicviewer.feature.folder.generated.resources.folder_sorttype_label_date_asc
import comicviewer.feature.folder.generated.resources.folder_sorttype_label_date_desc
import comicviewer.feature.folder.generated.resources.folder_sorttype_label_include_subfolders
import comicviewer.feature.folder.generated.resources.folder_sorttype_label_name_asc
import comicviewer.feature.folder.generated.resources.folder_sorttype_label_name_desc
import comicviewer.feature.folder.generated.resources.folder_sorttype_label_size_asc
import comicviewer.feature.folder.generated.resources.folder_sorttype_label_size_desc
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun SortTypeItem(
    sortType: SortType,
    sortScope: FolderSortScope,
    onSortScopeClick: (FolderSortScope) -> Unit,
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
        FolderSortScope.entries.forEach { scope ->
            DropdownMenuItem(
                text = {
                    Text(text = stringResource(scope.displayText()))
                },
                leadingIcon = {
                    RadioButton(
                        selected = scope == sortScope,
                        onClick = { onSortScopeClick(scope) },
                        modifier = Modifier.focusable(false),
                    )
                },
                onClick = {
                    onSortScopeClick(scope)
                },
            )
        }
        HorizontalDivider()
        SortType.entries.forEach { item ->
            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(item.menuText()),
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

private fun FolderSortScope.displayText() = when (this) {
    FolderSortScope.Global -> Res.string.folder_sorttype_label_apply_to_all
    FolderSortScope.FolderOnly -> Res.string.folder_sorttype_label_apply_to_folder_only
    FolderSortScope.Subfolders -> Res.string.folder_sorttype_label_include_subfolders
}

private fun SortType.displayText() = when (this) {
    is SortType.Date -> Res.string.folder_label_update_date
    is SortType.Name -> Res.string.folder_label_name
    is SortType.Size -> Res.string.folder_label_file_size
}

private fun SortType.menuText() = when (this) {
    is SortType.Name -> if (isAsc) {
        Res.string.folder_sorttype_label_name_asc
    } else {
        Res.string.folder_sorttype_label_name_desc
    }

    is SortType.Date -> if (isAsc) {
        Res.string.folder_sorttype_label_date_asc
    } else {
        Res.string.folder_sorttype_label_date_desc
    }

    is SortType.Size -> if (isAsc) {
        Res.string.folder_sorttype_label_size_asc
    } else {
        Res.string.folder_sorttype_label_size_desc
    }
}
