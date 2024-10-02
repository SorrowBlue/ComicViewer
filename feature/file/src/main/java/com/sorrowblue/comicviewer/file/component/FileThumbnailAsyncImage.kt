package com.sorrowblue.comicviewer.file.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImagePainter.Companion.DefaultTransform
import coil3.compose.AsyncImagePainter.State
import coil3.compose.SubcomposeAsyncImage
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.domain.model.file.FileThumbnail
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.preview.previewPainter

@Composable
fun FileThumbnailAsyncImage(
    fileThumbnail: FileThumbnail,
    modifier: Modifier = Modifier,
    transform: (State) -> State = DefaultTransform,
    contentScale: ContentScale = ContentScale.Fit,
) {
    SubcomposeAsyncImage(
        model = fileThumbnail,
        contentDescription = null,
        contentScale = contentScale,
        transform = transform,
        error = {
            if (LocalInspectionMode.current && it.result.throwable !is NoLocalInspectionModeError) {
                Image(
                    painter = previewPainter(),
                    contentDescription = null,
                    contentScale = contentScale
                )
            } else {
                Icon(
                    imageVector = ComicIcons.BrokenImage,
                    contentDescription = null,
                    modifier = Modifier.wrapContentSize()
                )
            }
        },
        loading = {
            if (LocalInspectionMode.current) {
                CircularProgressIndicator(
                    progress = { 0.7f },
                    strokeWidth = 2.dp,
                    modifier = Modifier.wrapContentSize()
                )
            } else {
                CircularProgressIndicator(
                    strokeWidth = 2.dp,
                    modifier = Modifier.wrapContentSize()
                )
            }
        },
        modifier = modifier
    )
}

@Composable
@Preview(showBackground = true)
private fun FileThumbnailAsyncImagePreview(
    @PreviewParameter(TransformProvider::class) transform: (State) -> State,
) {
    ComicTheme {
        FileThumbnailAsyncImage(
            fileThumbnail = BookThumbnail(BookshelfId(), "", 0, 0, 0),
            transform = transform,
            modifier = Modifier
                .width(186.dp)
                .height(64.dp)
        )
    }
}

private class TransformProvider : PreviewParameterProvider<(State) -> State> {
    override val values: Sequence<(State) -> State> = sequenceOf(
        { State.Loading(null) },
        { state ->
            (state as? State.Error)?.let {
                it.copy(result = it.result.copy(throwable = NoLocalInspectionModeError()))
            } ?: state
        },
        DefaultTransform
    )
}

private class NoLocalInspectionModeError : Throwable()
