package com.sorrowblue.comicviewer.feature.bookshelf.edit.component

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
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawBookshelves
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawServer
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import comicviewer.feature.bookshelf.edit.generated.resources.Res
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_selection_label_device
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_selection_label_device_detail
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_selection_label_smb
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_selection_label_smb_detail
import org.jetbrains.compose.resources.stringResource

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
                        when (type) {
                            BookshelfType.DEVICE -> Res.string.bookshelf_edit_selection_label_device
                            BookshelfType.SMB -> Res.string.bookshelf_edit_selection_label_smb
                        }
                    ),
                    style = ComicTheme.typography.bodyLarge
                )
                Text(
                    text = stringResource(
                        when (type) {
                            BookshelfType.DEVICE -> Res.string.bookshelf_edit_selection_label_device_detail
                            BookshelfType.SMB -> Res.string.bookshelf_edit_selection_label_smb_detail
                        }
                    ),
                    style = ComicTheme.typography.bodyMedium
                )
            }
        }
    }
}
