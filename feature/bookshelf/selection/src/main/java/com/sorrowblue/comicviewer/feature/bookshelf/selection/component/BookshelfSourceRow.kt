package com.sorrowblue.comicviewer.feature.bookshelf.selection.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.feature.bookshelf.selection.R
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawBookshelves
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawServer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookshelfSourceRow(
    modifier: Modifier = Modifier,
    type: BookshelfType = BookshelfType.DEVICE,
    onClick: () -> Unit = {},
) {
    OutlinedCard(onClick = onClick, modifier = modifier) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = stringResource(
                    id = when (type) {
                        BookshelfType.DEVICE -> R.string.bookshelf_manage_title_device
                        BookshelfType.SMB -> R.string.bookshelf_manage_title_smb
                    }
                )
            )
            Row {
                Text(
                    text = stringResource(
                        id = when (type) {
                            BookshelfType.DEVICE -> R.string.bookshelf_manage_desc_device
                            BookshelfType.SMB -> R.string.bookshelf_manage_desc_smb
                        }
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 8.dp, end = 8.dp)
                )
                Image(
                    imageVector = when (type) {
                        BookshelfType.DEVICE -> ComicIcons.UndrawBookshelves
                        BookshelfType.SMB -> ComicIcons.UndrawServer
                    },
                    contentDescription = null,
                    modifier = Modifier.size(100.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun BookshelfSourceRowPreview() {
    BookshelfSourceRow()
}
