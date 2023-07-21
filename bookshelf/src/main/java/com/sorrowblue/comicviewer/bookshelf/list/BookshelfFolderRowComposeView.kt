package com.sorrowblue.comicviewer.bookshelf.list

import android.content.Context
import android.util.AttributeSet
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AbstractComposeView
import com.sorrowblue.comicviewer.domain.entity.BookshelfFolder

@OptIn(ExperimentalFoundationApi::class)
class BookshelfFolderRowComposeView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AbstractComposeView(context, attrs) {

    var bookshelfFolder by mutableStateOf<BookshelfFolder?>(null)
    var onClick: () -> Unit = {}
    var onLongClick: (() -> Unit)? = null

    @Composable
    override fun Content() {
        val bookshelfFolder = bookshelfFolder
        if (bookshelfFolder != null) {
            BookshelfFolderRow(
                bookshelfFolder,
                Modifier
                    .fillMaxWidth()
                    .combinedClickable(onLongClick = onLongClick, onClick = onClick)
            )
        }
    }
}
