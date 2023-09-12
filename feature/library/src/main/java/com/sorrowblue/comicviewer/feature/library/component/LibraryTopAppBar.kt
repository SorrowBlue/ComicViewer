package com.sorrowblue.comicviewer.feature.library.component

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LibraryTopAppBar(
    scrollBehavior: TopAppBarScrollBehavior?
) {
    TopAppBar(
        title = { Text(text = "Library") },
        scrollBehavior = scrollBehavior
    )
}
