package com.sorrowblue.comicviewer.book.section

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.framework.resource.R

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
            Text(text = "本はありません")
            Image(
                painter = painterResource(id = R.drawable.ic_undraw_not_found_re_44w9),
                contentDescription = null,
                modifier = Modifier
                    .height(200.dp)
                    .requiredWidthIn(max = 400.dp)
            )
        }
    }
}
