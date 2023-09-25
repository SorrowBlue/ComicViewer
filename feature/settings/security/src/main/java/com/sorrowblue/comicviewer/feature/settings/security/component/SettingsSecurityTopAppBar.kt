package com.sorrowblue.comicviewer.feature.settings.security.component

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sorrowblue.comicviewer.feature.settings.security.R
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsFolderTopAppBar(
    onBackClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior?,
) {
    TopAppBar(
        title = { Text(stringResource(R.string.settings_security_title)) },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(imageVector = ComicIcons.ArrowBack, contentDescription = "Back")
            }
        },
        scrollBehavior = scrollBehavior
    )
}
