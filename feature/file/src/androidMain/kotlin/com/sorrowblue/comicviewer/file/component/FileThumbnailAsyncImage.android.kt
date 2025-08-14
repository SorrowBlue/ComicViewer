package com.sorrowblue.comicviewer.file.component

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeBookFile

@Composable
@Preview(showBackground = true)
private fun FileThumbnailAsyncImagePreview(
    @PreviewParameter(FileThumbnailAsyncImageProvider::class) state: FileThumbnailAsyncImageProvider.State,
) {
    val content = remember {
        movableContentOf {
            FileThumbnailAsyncImage(
                fileThumbnail = BookThumbnail.from(fakeBookFile()),
                loading = if (state == FileThumbnailAsyncImageProvider.State.Success) null else FileThumbnailAsyncImageDefault.loading,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(186.dp)
                    .height(64.dp)
            )
        }
    }
    when (state) {
        FileThumbnailAsyncImageProvider.State.Error -> ComicTheme(content = content)
        FileThumbnailAsyncImageProvider.State.Loading,
        FileThumbnailAsyncImageProvider.State.Success,
        -> PreviewTheme(content = content)
    }
}

private class FileThumbnailAsyncImageProvider :
    PreviewParameterProvider<FileThumbnailAsyncImageProvider.State> {

    override val values = State.entries.asSequence()

    enum class State {
        Loading,
        Error,
        Success,
    }
}
