package com.sorrowblue.comicviewer.feature.collection.add.section

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.framework.ui.material3.CloseIconButton
import comicviewer.feature.collection.add.generated.resources.Res
import comicviewer.feature.collection.add.generated.resources.collection_add_title
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun CollectionAddAppBar(
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = { Text(text = stringResource(Res.string.collection_add_title)) },
        actions = {
            CloseIconButton(onClick = onCloseClick)
        },
        windowInsets = WindowInsets(0),
        modifier = modifier
    )
}
