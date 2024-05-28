package com.sorrowblue.comicviewer.feature.library.googledrive.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.sorrowblue.comicviewer.domain.model.file.File

@Composable
internal fun FileListItem(file: File, onClick: () -> Unit, modifier: Modifier = Modifier) {
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
        modifier = modifier.clickable(onClick = onClick)
    )
}
