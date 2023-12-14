package com.sorrowblue.comicviewer.file.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.size
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.rememberDebugPlaceholder

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileListContent(
    file: File?,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
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
                LinearProgressIndicator(
                    progress = { file.lastPageRead.toFloat() / file.totalPageCount },
                )
            }
        },
        leadingContent = {
            AsyncImage(
                model = file,
                modifier = Modifier.size(56.dp),
                contentDescription = null,
                placeholder = rememberDebugPlaceholder()
            )
        },
        trailingContent = {
            if (file is Book && 0 < file.totalPageCount) {
                Text("${file.totalPageCount}")
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileListMedium(
    file: File?,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ListItem(
        modifier = modifier.combinedClickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = rememberRipple(),
            onLongClick = onLongClick,
            onClick = onClick
        ),
        headlineContent = {
            Text(file?.name.orEmpty())
        },
        supportingContent = {
            if (file is Book && 0 < file.lastPageRead) {
                LinearProgressIndicator(
                    progress = { file.lastPageRead.toFloat() / file.totalPageCount },
                )
            }
        },
        leadingContent = {
            AsyncImage(
                model = file,
                modifier = Modifier.size(56.dp),
                contentDescription = null,
                placeholder = rememberDebugPlaceholder()
            )
        },
        trailingContent = {
            if (file is Book && 0 < file.totalPageCount) {
                Text("${file.totalPageCount}")
            }
        }
    )
}

@Preview
@Composable
private fun PreviewFileList() {
    ComicTheme {
        FileListContent(
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

internal val FakeFile2 = BookFile(
    BookshelfId(0),
    "1234567890123456789012345678901234567890",
    "/comic/example/",
    "/comic/example/FakeBookName.zip",
    0,
    0,
    "",
    50,
    123,
    0
)
