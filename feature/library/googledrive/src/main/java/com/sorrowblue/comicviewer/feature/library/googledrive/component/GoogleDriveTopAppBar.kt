package com.sorrowblue.comicviewer.feature.library.googledrive.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons

@Composable
internal fun GoogleDriveTopAppBar(
    profileUri: String,
    onBackClick: () -> Unit,
    onProfileImageClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    androidx.compose.material3.TopAppBar(
        title = {
            Text(text = stringResource(id = com.sorrowblue.comicviewer.app.R.string.googledrive_title))
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = ComicIcons.ArrowBack,
                    contentDescription = null
                )
            }
        },
        actions = {
            AsyncImage(
                model = profileUri,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .padding(9.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onProfileImageClick)
            )
        },
        windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top),
        scrollBehavior = scrollBehavior
    )
}
