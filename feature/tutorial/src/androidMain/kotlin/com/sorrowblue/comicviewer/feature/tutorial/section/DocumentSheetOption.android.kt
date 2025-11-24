package com.sorrowblue.comicviewer.feature.tutorial.section

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.domain.usecase.PdfPluginState
import com.sorrowblue.comicviewer.framework.common.LocalPlatformContext
import com.sorrowblue.comicviewer.framework.common.require
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import comicviewer.feature.tutorial.generated.resources.Res
import comicviewer.feature.tutorial.generated.resources.tutorial_label_already_installed
import comicviewer.feature.tutorial.generated.resources.tutorial_label_not_installed
import comicviewer.feature.tutorial.generated.resources.tutorial_label_old_version
import comicviewer.feature.tutorial.generated.resources.tutorial_label_open_play_store
import io.github.takahirom.rin.rememberRetained
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

internal data class DocumentSheetOptionUiState(
    val pluginState: PdfPluginState = PdfPluginState.NotInstalled,
)

@Composable
internal actual fun DocumentSheetOption(modifier: Modifier) {
    val context = LocalPlatformContext.current
    val state =
        with(rememberRetained { context.require<DocumentSheetOptionContext.Factory>().create() }) {
            rememberDocumentSheetOptionState()
        }
    DocumentSheetOption(
        uiState = state.uiState,
        onOpenLinkClick = state::onOpenLinkClick,
        modifier = modifier,
    )
}

@Composable
private fun DocumentSheetOption(
    uiState: DocumentSheetOptionUiState,
    onOpenLinkClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(uiState.pluginState.statusLabelResource),
            style = ComicTheme.typography.bodyMedium,
        )
        Spacer(Modifier.size(8.dp))
        OutlinedButton(
            onClick = onOpenLinkClick,
            contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
        ) {
            Icon(ComicIcons.OpenInBrowser, null)
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = stringResource(Res.string.tutorial_label_open_play_store))
        }
    }
}

private val PdfPluginState.statusLabelResource: StringResource
    get() = when (this) {
        PdfPluginState.Enable -> Res.string.tutorial_label_already_installed
        PdfPluginState.OldVersion -> Res.string.tutorial_label_old_version
        PdfPluginState.NotInstalled -> Res.string.tutorial_label_not_installed
    }
