package com.sorrowblue.comicviewer.feature.bookshelf.component

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.CanonicalTopAppBar
import com.sorrowblue.comicviewer.framework.ui.material3.SettingsIconButton
import comicviewer.feature.bookshelf.generated.resources.Res
import comicviewer.feature.bookshelf.generated.resources.bookshelf_label_bookshelf
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun BookshelfAppBar(
    onSettingsClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    scrollableState: ScrollableState,
    modifier: Modifier = Modifier,
) {
    CanonicalTopAppBar(
        title = {
            Text(text = stringResource(Res.string.bookshelf_label_bookshelf))
        },
        actions = {
            SettingsIconButton(onClick = onSettingsClick)
        },
        scrollBehavior = scrollBehavior,
        scrollableState = scrollableState,
        modifier = modifier,
    )
}
