package com.sorrowblue.comicviewer.file.component

import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImagePainter.State
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageScope
import com.sorrowblue.comicviewer.domain.model.file.FileThumbnail
import com.sorrowblue.comicviewer.domain.model.file.FolderThumbnail
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons

@Composable
fun FileThumbnailAsyncImage(
    fileThumbnail: FileThumbnail,
    modifier: Modifier = Modifier,
    error: @Composable (SubcomposeAsyncImageScope.(State.Error) -> Unit)? = {
        Icon(
            imageVector = if (fileThumbnail is FolderThumbnail) ComicIcons.FolderOff else ComicIcons.BrokenImage,
            contentDescription = null,
            modifier = Modifier.wrapContentSize()
        )
    },
    loading: @Composable (SubcomposeAsyncImageScope.(State.Loading) -> Unit)? =
        if (LocalInspectionMode.current) null else FileThumbnailAsyncImageDefault.loading,
    onError: ((State.Error) -> Unit)? = null,
    onSuccess: ((State.Success) -> Unit)? = null,
    filterQuality: FilterQuality = FilterQuality.None,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
) {
    SubcomposeAsyncImage(
        model = fileThumbnail,
        contentDescription = null,
        contentScale = contentScale,
        filterQuality = filterQuality,
        alignment = alignment,
        error = error,
        loading = loading,
        modifier = modifier,
        onError = onError,
        onSuccess = onSuccess
    )
}

internal object FileThumbnailAsyncImageDefault {
    val loading: @Composable SubcomposeAsyncImageScope.(State.Loading) -> Unit = {
        CircularProgressIndicator(
            strokeWidth = 2.dp,
            modifier = Modifier.wrapContentSize()
        )
    }
}
