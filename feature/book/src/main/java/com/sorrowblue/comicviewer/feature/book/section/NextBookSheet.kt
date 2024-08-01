package com.sorrowblue.comicviewer.feature.book.section

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.feature.book.R
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawTaken
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.preview.PreviewMultiScreen
import com.sorrowblue.comicviewer.framework.preview.PreviewTheme
import com.sorrowblue.comicviewer.framework.preview.fakeBookFile
import com.sorrowblue.comicviewer.framework.preview.previewPlaceholder
import kotlinx.collections.immutable.toPersistentList

@Composable
internal fun NextBookSheet(
    nextPage: NextPage,
    onClick: (Book) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(ComicTheme.dimension.margin),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
    ) {
        if (nextPage.nextBooks.isNotEmpty()) {
            nextPage.nextBooks.forEach {
                OtherBook(it) {
                    onClick(it.book)
                }
            }
        } else {
            Image(
                imageVector = ComicIcons.UndrawTaken,
                contentDescription = null,
                modifier = Modifier
                    .sizeIn(maxWidth = 300.dp, maxHeight = 300.dp)
                    .fillMaxWidth(0.5f)
            )
            Spacer(modifier = Modifier.size(ComicTheme.dimension.minPadding))
            Text(
                text = stringResource(id = R.string.book_label_no_next_book),
                style = ComicTheme.typography.titleLarge
            )
        }
    }
}

@Composable
private fun OtherBook(nextBook: NextBook, onClick: () -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .widthIn(max = 480.dp)
            .fillMaxWidth(1f)
    ) {
        Text(
            text = when (nextBook) {
                is NextBook.Favorite -> stringResource(id = R.string.book_text_favorite_book)
                is NextBook.Folder -> stringResource(id = R.string.book_text_folder_book)
            },
            style = ComicTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp)
        )
        AsyncImage(
            model = nextBook.book,
            error = previewPlaceholder(),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp)
                .heightIn(max = 200.dp)
                .fillMaxWidth(0.5f)
        )
        Text(
            text = nextBook.book.name,
            style = ComicTheme.typography.bodyMedium,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp)
                .align(Alignment.CenterHorizontally)
        )
        FilledTonalButton(
            onClick = onClick,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 8.dp, bottom = 16.dp)
        ) {
            Text(text = stringResource(id = R.string.book_action_read))
        }
    }
}

@PreviewMultiScreen
@Composable
private fun PreviewNextBookSheet() {
    PreviewTheme {
        Surface(Modifier.fillMaxSize()) {
            NextBookSheet(
                NextPage(nextBooks = listOf(NextBook.Folder(fakeBookFile())).toPersistentList()),
                {}
            )
        }
    }
}
