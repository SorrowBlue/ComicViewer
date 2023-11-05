package com.sorrowblue.comicviewer.feature.settings.common

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.material3.TopAppBar
import com.sorrowblue.comicviewer.framework.ui.material3.TopAppBarScrollBehavior

@Composable
fun SettingsTopAppBar(
    title: Int,
    onBackClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    TopAppBar(
        title = { Text(text = stringResource(id = title)) },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(imageVector = ComicIcons.ArrowBack, contentDescription = "Back")
            }
        },
        scrollBehavior = scrollBehavior
    )
}
