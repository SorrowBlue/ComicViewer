package com.sorrowblue.comicviewer.folder.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.framework.compose.placeholder.debugPlaceholder
import com.sorrowblue.comicviewer.framework.compose.placeholder.placeholder3

@Composable
fun FileList(file: File?, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.padding(start = 16.dp, top = 8.dp, end = 24.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = file,
            contentDescription = "",
            modifier = Modifier
                .size(56.dp)
                .placeholder3(file == null),
            placeholder = debugPlaceholder()
        )
        Spacer(Modifier.size(16.dp))
        Text(
            file?.name.orEmpty(),
            Modifier
                .weight(1f, true)
                .placeholder3(file == null),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(Modifier.size(16.dp))
        if (file is Book && 0 < file.totalPageCount) {
            Text(
                "${file.totalPageCount}",
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}
