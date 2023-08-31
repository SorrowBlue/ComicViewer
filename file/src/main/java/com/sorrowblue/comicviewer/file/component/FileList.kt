package com.sorrowblue.comicviewer.file.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.size
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.domain.entity.file.BookFile
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.framework.compose.AppMaterialTheme
import com.sorrowblue.comicviewer.framework.compose.placeholder.debugPlaceholder

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileList(
    file: File?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    ElevatedCard {
        ListItem(
            modifier = modifier.combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(),
                onLongClick = onLongClick,
                onClick = onClick
            ),
            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
            headlineContent = {
                Text(file?.name.orEmpty())
            },
            supportingContent = {
                if (file is Book && 0 < file.lastPageRead) {
                    LinearProgressIndicator(progress = file.lastPageRead.toFloat() / file.totalPageCount)
                }
            },
            leadingContent = {
                AsyncImage(
                    model = file,
                    modifier = Modifier.size(56.dp),
                    contentDescription = "",
                    placeholder = debugPlaceholder()
                )
            },
            trailingContent = {
                if (file is Book && 0 < file.totalPageCount) {
                    Text("${file.totalPageCount}")
                }
            }
        )
    }
}

@Preview
@Composable
private fun PreviewFileList() {
    AppMaterialTheme {
        FileList(
            file = FakeFile,
            onClick = {},
            onLongClick = {}
        )
    }
}

internal val FakeFile = BookFile(
    BookshelfId(0),
    "FakeBookName.zip",
    "/comic/example/",
    "/comic/example/FakeBookName.zip",
    0,
    0,
    "",
    50,
    123,
    0
)
