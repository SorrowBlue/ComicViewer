package com.sorrowblue.comicviewer.feature.book.section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.feature.book.R
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawTaken
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.material3.Button
import com.sorrowblue.comicviewer.framework.ui.material3.Text

@Composable
internal fun NextBookSheet(
    nextBook: NextBook,
    onClick: (Book) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val book = nextBook.book
        if (book != null) {
            Text(text = book.name)
            AsyncImage(
                model = book,
                contentDescription = null,
                modifier = Modifier.height(300.dp)
            )
            Button(
                id = if (nextBook is NextBook.Next) R.string.book_label_open_next_book else R.string.book_label_open_prev_book,
                onClick = { onClick(book) }
            )
        } else {
            AsyncImage(
                model = ComicIcons.UndrawTaken,
                contentDescription = null,
                modifier = Modifier
                    .sizeIn(maxWidth = 300.dp, maxHeight = 300.dp)
                    .fillMaxWidth(0.5f)
            )
            Spacer(modifier = Modifier.size(ComicTheme.dimension.padding))
            Text(
                id = if (nextBook is NextBook.Next) R.string.book_label_no_next_book else R.string.book_label_no_prev_book,
                style = ComicTheme.typography.titleLarge
            )
        }
    }
}
