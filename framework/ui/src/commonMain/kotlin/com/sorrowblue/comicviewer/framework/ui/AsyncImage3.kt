package com.sorrowblue.comicviewer.framework.ui

import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
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

@Composable
fun AsyncImage3(
    model: Any?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    error: @Composable (SubcomposeAsyncImageScope.(State.Error) -> Unit)? = null,
    loading: @Composable (SubcomposeAsyncImageScope.(State.Loading) -> Unit)? = AsyncImage3Default.loading,
    onError: ((State.Error) -> Unit)? = null,
    onSuccess: ((State.Success) -> Unit)? = null,
    filterQuality: FilterQuality = FilterQuality.None,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
) {
    SubcomposeAsyncImage(
        model = model,
        contentDescription = contentDescription,
        contentScale = contentScale,
        filterQuality = filterQuality,
        alignment = alignment,
        error = error,
        loading = if (LocalInspectionMode.current) null else loading,
        modifier = modifier,
        onError = onError,
        onSuccess = onSuccess
    )
}

internal object AsyncImage3Default {
    val loading: @Composable SubcomposeAsyncImageScope.(State.Loading) -> Unit = {
        CircularProgressIndicator(
            strokeWidth = 2.dp,
            modifier = Modifier.wrapContentSize()
        )
    }
}
