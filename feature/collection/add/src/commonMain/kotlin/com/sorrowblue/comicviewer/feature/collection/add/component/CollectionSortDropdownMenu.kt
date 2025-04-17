package com.sorrowblue.comicviewer.feature.collection.add.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import comicviewer.feature.collection.add.generated.resources.Res
import comicviewer.feature.collection.add.generated.resources.collection_add_label_created
import comicviewer.feature.collection.add.generated.resources.collection_add_label_recent
import comicviewer.feature.collection.add.generated.resources.collection_add_label_sort
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

internal enum class CollectionSort(val labelRes: StringResource) {
    Recent(Res.string.collection_add_label_recent),
    Created(Res.string.collection_add_label_created),
}

@Composable
internal fun CollectionSortDropdownMenu(
    collectionSort: CollectionSort,
    onClick: (CollectionSort) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        TextButton(
            onClick = { expanded = true },
            contentPadding = ButtonDefaults.TextButtonWithIconContentPadding,
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
        ) {
            Text(stringResource(Res.string.collection_add_label_sort))
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Icon(ComicIcons.Sort, contentDescription = null)
        }
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            val collectionSorts = remember { CollectionSort.entries }
            collectionSorts.forEach { item ->
                DropdownMenuItem(
                    text = { Text(stringResource(item.labelRes)) },
                    onClick = {
                        onClick(item)
                        expanded = false
                    },
                    trailingIcon = {
                        if (collectionSort == item) {
                            Icon(ComicIcons.Check, null)
                        }
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}
