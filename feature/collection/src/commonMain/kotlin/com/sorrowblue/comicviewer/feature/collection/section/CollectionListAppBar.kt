package com.sorrowblue.comicviewer.feature.collection.section

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.framework.ui.CanonicalScaffoldState
import com.sorrowblue.comicviewer.framework.ui.canonical.CanonicalAppBar
import com.sorrowblue.comicviewer.framework.ui.material3.SettingsIconButton
import comicviewer.feature.collection.generated.resources.Res
import comicviewer.feature.collection.generated.resources.collection_title
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun CanonicalScaffoldState<*>.CollectionListAppBar(onSettingsClick: () -> Unit) {
    CanonicalAppBar(
        title = {
            Text(text = stringResource(Res.string.collection_title))
        },
        actions = {
            SettingsIconButton(onClick = onSettingsClick)
        },
    )
}
