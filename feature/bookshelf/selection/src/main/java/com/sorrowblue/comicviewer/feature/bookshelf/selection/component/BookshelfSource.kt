package com.sorrowblue.comicviewer.feature.bookshelf.selection.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.feature.bookshelf.selection.R
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawBookshelves
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawServer
import com.sorrowblue.comicviewer.framework.ui.material3.OutlinedCard
import com.sorrowblue.comicviewer.framework.ui.material3.Text

@Composable
internal fun BookshelfSource(
    modifier: Modifier = Modifier,
    type: BookshelfType = BookshelfType.DEVICE,
    onClick: () -> Unit = {},
) {
    OutlinedCard(
        onClick = onClick,
        modifier = modifier
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                id = when (type) {
                    BookshelfType.DEVICE -> R.string.bookshelf_selection_title_device
                    BookshelfType.SMB -> R.string.bookshelf_selection_title_smb
                }
            )
            Row {
                Text(
                    id = when (type) {
                        BookshelfType.DEVICE -> R.string.bookshelf_selection_desc_device
                        BookshelfType.SMB -> R.string.bookshelf_selection_desc_smb
                    },
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
