package com.sorrowblue.comicviewer.feature.library.googledrive.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sorrowblue.comicviewer.framework.ui.material3.TopAppBar
import com.sorrowblue.comicviewer.framework.ui.material3.TopAppBarScrollBehavior

@Composable
internal fun GoogleDriveTopAppBar(
    profileUri: String,
    onBackClick: () -> Unit,
    onProfileImageClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior?,
) {
    TopAppBar(
        title = "GoogleDrive",
        onBackClick = onBackClick,
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
        scrollBehavior = scrollBehavior
    )
}
