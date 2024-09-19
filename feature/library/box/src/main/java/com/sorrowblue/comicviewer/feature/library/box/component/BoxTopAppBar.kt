package com.sorrowblue.comicviewer.feature.library.box.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.AsyncImage
import coil3.network.NetworkHeaders
import coil3.network.httpHeaders
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.toUri
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.preview.previewPlaceholder
import logcat.logcat

@OptIn(ExperimentalCoilApi::class)
@Composable
internal fun BoxTopAppBar(
    path: String,
    profileUri: String,
    token: String,
    onBackClick: () -> Unit,
    onProfileImageClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior?,
) {
    TopAppBar(
        title = {
            Column {
                Text(text = stringResource(com.sorrowblue.comicviewer.app.R.string.box_title))
                if (path.isNotEmpty()) {
                    Text(
                        text = path,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(imageVector = ComicIcons.ArrowBack, contentDescription = "Back")
            }
        },
        actions = {
            if (profileUri.isNotEmpty()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data("https://avatars.githubusercontent.com/u/25735189?v=4".toUri())
                        .httpHeaders(
                            NetworkHeaders.Builder().add("Authorization", "Bearer $token").build()
                        )
                        .crossfade(true)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .listener { request, result ->
                            logcat { "request=$request, result=$result" }
                        }
                        .build(),
                    error = previewPlaceholder(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .padding(9.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onProfileImageClick)
                )
            }
        },
        windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top),
        scrollBehavior = scrollBehavior
    )
}
