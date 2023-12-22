package com.sorrowblue.comicviewer.feature.history.section

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sorrowblue.comicviewer.feature.history.R
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HistoryAppBar(
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    TopAppBar(
        title = { Text(stringResource(R.string.history_title)) },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(imageVector = ComicIcons.ArrowBack, contentDescription = "Back")
            }
        },
        actions = {
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = ComicIcons.Settings,
                    contentDescription = stringResource(id = com.sorrowblue.comicviewer.framework.ui.R.string.framework_ui_desc_open_settings)
                )
            }
        },
        scrollBehavior = scrollBehavior
    )
}
