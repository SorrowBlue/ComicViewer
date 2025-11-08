package com.sorrowblue.comicviewer.feature.collection.component

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import comicviewer.feature.collection.generated.resources.Res
import comicviewer.feature.collection.generated.resources.collection_label_delete
import comicviewer.feature.collection.generated.resources.collection_label_edit
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun CollectionActionsDropdown(onEditClick: () -> Unit, onDeleteClick: () -> Unit) {
    Box {
        var expanded by rememberSaveable { mutableStateOf(false) }
        IconButton(onClick = { expanded = !expanded }) {
            Icon(ComicIcons.MoreVert, null)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(Res.string.collection_label_edit)) },
                leadingIcon = { Icon(ComicIcons.Edit, null) },
                onClick = {
                    expanded = false
                    onEditClick()
                },
            )
            DropdownMenuItem(
                text = { Text(stringResource(Res.string.collection_label_delete)) },
                leadingIcon = { Icon(ComicIcons.Delete, null) },
                onClick = {
                    expanded = false
                    onDeleteClick()
                },
            )
        }
    }
}
