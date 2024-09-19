package com.sorrowblue.comicviewer.bookshelf.section

import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sorrowblue.comicviewer.feature.bookshelf.R
import com.sorrowblue.comicviewer.framework.ui.adaptive.CanonicalTopAppBar
import com.sorrowblue.comicviewer.framework.ui.material3.SettingsIconButton

@Composable
internal fun BookshelfAppBar(
    onSettingsClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier,
) {
    CanonicalTopAppBar(
        title = {
            Text(text = stringResource(id = R.string.bookshelf_list_title))
        },
        actions = {
            SettingsIconButton(onClick = onSettingsClick)
        },
        scrollBehavior = scrollBehavior,
        modifier = modifier,
    )
}
