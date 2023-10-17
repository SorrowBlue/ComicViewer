package com.sorrowblue.comicviewer.feature.book.section

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawTaken
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme

@Composable
internal fun NextBookSheet(book: Book?, isNext: Boolean = true, onClick: (Book) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (book != null) {
            Text(text = book.name)
            AsyncImage(
                model = book,
                contentDescription = null,
                modifier = Modifier.height(300.dp)
            )
            Button(onClick = { onClick(book) }) {
                Text(text = if (isNext) "次の本" else "前の本")
            }
        } else {
            Image(
                imageVector = ComicIcons.UndrawTaken,
                contentDescription = null,
                modifier = Modifier
                    .sizeIn(maxWidth = 300.dp, maxHeight = 300.dp)
                    .fillMaxWidth(0.5f)
            )
            Spacer(modifier = Modifier.size(ComicTheme.dimension.padding))
            Text(text = "本はありません", style = ComicTheme.typography.titleLarge)
        }
    }
}
