package com.sorrowblue.comicviewer.feature.book.section

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewMultiScreen
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeBookFile

@PreviewMultiScreen
@Composable
private fun PreviewNextBookSheet() {
    PreviewTheme {
        Surface(Modifier.fillMaxSize()) {
            NextBookSheet(
                NextPage(isNext = true, nextBooks = listOf(NextBook.Folder(fakeBookFile()))),
                {}
            )
        }
    }
}
