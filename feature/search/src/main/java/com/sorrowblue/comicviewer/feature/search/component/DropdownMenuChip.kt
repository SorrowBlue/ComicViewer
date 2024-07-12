package com.sorrowblue.comicviewer.feature.search.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import kotlinx.collections.immutable.PersistentList

@Composable
fun <T> DropdownMenuChip(
    text: String,
    onChangeSelect: (T) -> Unit,
    menus: PersistentList<T>,
    menu: @Composable (T) -> String,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.wrapContentSize(Alignment.TopStart)) {
        var expanded by remember { mutableStateOf(false) }
        FilterChip(
            selected = false,
            onClick = { expanded = true },
            label = { Text(text = text) },
            trailingIcon = {
                Icon(
                    imageVector = ComicIcons.ArrowDropDown,
                    contentDescription = null
                )
            },
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            menus.forEach {
                DropdownMenuItem(
                    text = { Text(text = menu(it)) },
                    onClick = {
                        onChangeSelect(it)
                        expanded = false
                    }
                )
            }
        }
    }
}
