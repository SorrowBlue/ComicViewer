package com.sorrowblue.comicviewer.folder.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.framework.compose.AppMaterialTheme
import com.sorrowblue.comicviewer.framework.compose.placeholder.placeholder3

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileList(file: File?, onClick: (File) -> Unit) {
    Row(
        Modifier
            .combinedClickable(onClick = { file?.let(onClick) })
            .padding(start = 16.dp, top = 8.dp, end = 24.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = file,
            contentDescription = "",
            Modifier
                .size(56.dp)
                .placeholder3(file == null)
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

@Preview(showBackground = true)
@Composable
fun PreviewFileList() {
    AppMaterialTheme {
        FileList(file = null, onClick = {})
    }
}
