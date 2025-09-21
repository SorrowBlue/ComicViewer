package com.sorrowblue.comicviewer.feature.tutorial.section

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import comicviewer.feature.tutorial.generated.resources.Res
import comicviewer.feature.tutorial.generated.resources.tutorial_msg_not_supported
import org.jetbrains.compose.resources.stringResource

@Composable
internal actual fun DocumentSheetOption(modifier: Modifier) {
    Text(stringResource(Res.string.tutorial_msg_not_supported))
}
