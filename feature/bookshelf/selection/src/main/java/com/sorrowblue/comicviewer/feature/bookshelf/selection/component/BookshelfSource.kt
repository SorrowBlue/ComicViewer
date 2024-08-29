package com.sorrowblue.comicviewer.feature.bookshelf.selection.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.feature.bookshelf.selection.R
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawBookshelves
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawServer
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme

@Composable
internal fun BookshelfSource(
    type: BookshelfType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(onClick = onClick, modifier = modifier) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Image(
                imageVector = when (type) {
                    BookshelfType.DEVICE -> ComicIcons.UndrawBookshelves
                    BookshelfType.SMB -> ComicIcons.UndrawServer
                },
                contentDescription = null,
                modifier = Modifier.size(100.dp)
            )
            Column(
                Modifier
                    .fillMaxHeight()
                    .padding(start = 16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(
                        id = when (type) {
                            BookshelfType.DEVICE -> R.string.bookshelf_selection_title_device
                            BookshelfType.SMB -> R.string.bookshelf_selection_title_smb
                        }
                    ),
                    style = ComicTheme.typography.bodyLarge
                )
                Text(
                    text = stringResource(
                        id = when (type) {
                            BookshelfType.DEVICE -> R.string.bookshelf_selection_desc_device
                            BookshelfType.SMB -> R.string.bookshelf_selection_desc_smb
                        }
                    ),
                    style = ComicTheme.typography.bodyMedium
                )
            }
        }
    }
}
