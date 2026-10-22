/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.framework.ui.component.file

import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImagePainter.State
import coil3.compose.SubcomposeAsyncImageScope
import com.sorrowblue.comicviewer.domain.model.file.FileThumbnail
import com.sorrowblue.comicviewer.domain.model.file.FolderThumbnail
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.AsyncImage3
import com.sorrowblue.comicviewer.framework.ui.AsyncImage3Default

@Composable
fun FileThumbnailAsyncImage(
    fileThumbnail: FileThumbnail,
    modifier: Modifier = Modifier,
    error: @Composable (SubcomposeAsyncImageScope.(State.Error) -> Unit)? = {
        Icon(
            imageVector = if (fileThumbnail is FolderThumbnail) ComicIcons.FolderOff else ComicIcons.BrokenImage,
            contentDescription = null,
            modifier = Modifier.wrapContentSize(),
        )
    },
    loading: @Composable (SubcomposeAsyncImageScope.(State.Loading) -> Unit)? =
        AsyncImage3Default.loading,
    onError: ((State.Error) -> Unit)? = null,
    onSuccess: ((State.Success) -> Unit)? = null,
    filterQuality: FilterQuality = FilterQuality.None,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
) {
    AsyncImage3(
        model = fileThumbnail,
        contentDescription = null,
        contentScale = contentScale,
        filterQuality = filterQuality,
        alignment = alignment,
        error = error,
        loading = loading,
        modifier = modifier,
        onError = onError,
        onSuccess = onSuccess,
    )
}
