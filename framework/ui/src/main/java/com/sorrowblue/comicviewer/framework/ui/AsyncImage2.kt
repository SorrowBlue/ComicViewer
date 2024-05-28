package com.sorrowblue.comicviewer.framework.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage

@Composable
fun AsyncImage2(
    model: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    placeholder: Painter? = null,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    loading: @Composable (() -> Unit)? = null,
    error: @Composable (() -> Unit)? = null,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        var isError by remember(model) { mutableStateOf(false) }
        var isLoading by remember(model) { mutableStateOf(false) }
        AsyncImage(
            model = model,
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize(),
            alignment = alignment,
            contentScale = contentScale,
            placeholder = placeholder,
            onError = {
                isLoading = false
                isError = true
            },
            onLoading = {
                isLoading = true
                isError = false
            },
            onSuccess = {
                isLoading = false
                isError = false
            },
        )
        AnimatedVisibility(visible = isLoading && loading != null) {
            loading?.invoke()
        }
        AnimatedVisibility(visible = isError && error != null) {
            error?.invoke()
        }
    }
}
