package com.sorrowblue.comicviewer.feature.library.component

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sorrowblue.comicviewer.feature.library.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LibraryTopAppBar(
    scrollBehavior: TopAppBarScrollBehavior?,
) {
    TopAppBar(
        title = { Text(text = stringResource(id = R.string.library_title)) },
        scrollBehavior = scrollBehavior
    )
}
