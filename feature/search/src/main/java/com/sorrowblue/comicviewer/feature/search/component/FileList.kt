package com.sorrowblue.comicviewer.feature.search.component

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Book
import androidx.compose.material.icons.twotone.BrokenImage
import androidx.compose.material.icons.twotone.Folder
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.ComicPreviews
import com.sorrowblue.comicviewer.framework.ui.debugPlaceholder

@Composable
internal fun FileList(
    file: File?,
    modifier: Modifier = Modifier,
) {
    ListItem(
        headlineContent = {
            Text(text = file?.name.orEmpty())
        },
        leadingContent = {
            AsyncImage(
                model = file,
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp)),
                placeholder = debugPlaceholder(),
                error = rememberVectorPainter(Icons.TwoTone.BrokenImage),
            )
        },
        supportingContent = {
            when (file) {
                is Book -> {
                    if (0 < file.totalPageCount) {
                        Text(text = "${file.totalPageCount} pages")
                    }
                }

                is Folder -> {
                    if (0 < file.count) {
                        Text(text = "${file.count} files")
                    }
                }

                null -> Unit
            }
        },
        trailingContent = {
            when (file) {
                is Book -> Icon(
                    painter = rememberVectorPainter(image = Icons.TwoTone.Book),
                    contentDescription = null
                )

                is Folder -> Icon(
                    painter = rememberVectorPainter(image = Icons.TwoTone.Folder),
                    contentDescription = null
                )

                null -> Unit
            }
        },
        modifier = modifier
    )
}

@ComicPreviews
@Composable
private fun PreviewFileList() {
    ComicTheme {
        FileList(null)
    }
}
