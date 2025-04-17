package com.sorrowblue.comicviewer.feature.collection.section

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.CanonicalTopAppBar
import com.sorrowblue.comicviewer.framework.ui.material3.SettingsIconButton
import comicviewer.feature.collection.generated.resources.Res
import comicviewer.feature.collection.generated.resources.collection_title
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun CollectionListAppBar(
    onSettingsClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    scrollableState: ScrollableState,
) {
    CanonicalTopAppBar(
        title = {
            Text(text = stringResource(Res.string.collection_title))
        },
        actions = {
            SettingsIconButton(onClick = onSettingsClick)
        },
        scrollBehavior = scrollBehavior,
        scrollableState = scrollableState
    )
}
