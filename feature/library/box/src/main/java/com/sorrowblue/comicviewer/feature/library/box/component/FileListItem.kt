package com.sorrowblue.comicviewer.feature.library.box.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.framework.ui.material3.placeholder3

@Composable
fun FileListItem(file: File, onClick: () -> Unit) {
    ListItem(
        headlineContent = { Text(text = file.name) },
        trailingContent = {
            Text(text = file.size.toString())
        },
        leadingContent = {
            AsyncImage(
                model = file.params["iconLink"],
                contentDescription = null,
                Modifier.size(24.dp)
            )
        },
        modifier = Modifier.clickable(onClick = onClick)
    )
}

@Composable
fun FileListItemSh() {
    ListItem(
        headlineContent = {
            Text(
                text = "aaaaaaaaaaaaaaaaaa",
                modifier = Modifier
                    .fillMaxWidth()
                    .placeholder3(true)
            )
        },
        trailingContent = {
            Text(
                text = "aaa",
                modifier = Modifier
                    .placeholder3(true)
            )
        },
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .placeholder3(true)
            )
        },
    )
}
