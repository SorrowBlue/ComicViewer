package com.sorrowblue.comicviewer.library.box.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
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
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.sorrowblue.comicviewer.framework.compose.placeholder.debugPlaceholder
import com.sorrowblue.comicviewer.library.box.R

@ExperimentalMaterial3Api
@Composable
internal fun BoxTopAppBar(
    path: String,
    profileUri: String,
    token: String,
    onBackClick: () -> Unit,
    onProfileImageClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior?
) {
    TopAppBar(
        title = {
            Column {
                Text(text = stringResource(R.string.box_title))
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
                Icon(imageVector = Icons.TwoTone.ArrowBack, contentDescription = "Back")
            }
        },
        actions = {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(profileUri)
                    .addHeader("Authorization", "Bearer $token")
                    .crossfade(true)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .build(),
                placeholder = debugPlaceholder(),
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .padding(9.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onProfileImageClick)
            )
        },
        scrollBehavior = scrollBehavior
    )
}
