package com.sorrowblue.comicviewer.feature.history.section

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sorrowblue.comicviewer.feature.history.R
import com.sorrowblue.comicviewer.framework.ui.material3.BackButton
import com.sorrowblue.comicviewer.framework.ui.material3.SettingsButton

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
            BackButton(onClick = onBackClick)
        },
        actions = {
            SettingsButton(onClick = onSettingsClick)
        },
        windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top),
        scrollBehavior = scrollBehavior
    )
}
