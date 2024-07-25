package com.sorrowblue.comicviewer.feature.tutorial.section

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.feature.tutorial.R
import com.sorrowblue.comicviewer.feature.tutorial.SplitInstallError
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawResumeFolder
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.preview.PreviewMultiScreen
import com.sorrowblue.comicviewer.framework.preview.PreviewTheme

internal sealed interface DocumentSheetUiState {

    val isButtonEnabled: Boolean
    val buttonText: String

    val isProgressVisible: Boolean
    val progress: Float

    data object Pending : DocumentSheetUiState {
        override val isButtonEnabled = false
        override val buttonText = "PENDING"
        override val isProgressVisible = true
        override val progress = 0f
    }

    data object RequestsUserConfirmation : DocumentSheetUiState {
        override val isButtonEnabled = false
        override val buttonText = "REQUIRES_USER_CONFIRMATION"
        override val isProgressVisible = true
        override val progress = 0f
    }

    data class DOWNLOADING(override val progress: Float) : DocumentSheetUiState {
        override val isButtonEnabled = false
        override val buttonText = "DOWNLOADING"
        override val isProgressVisible = true
    }

    data object DOWNLOADED : DocumentSheetUiState {
        override val isButtonEnabled = false
        override val buttonText = "DOWNLOADED"
        override val isProgressVisible = true
        override val progress = 0f
    }

    data object INSTALLING : DocumentSheetUiState {
        override val isButtonEnabled = false
        override val buttonText = "INSTALLING"
        override val isProgressVisible = true
        override val progress = 0f
    }

    data object INSTALLED : DocumentSheetUiState {
        override val isButtonEnabled = false
        override val buttonText = "INSTALLED"
        override val isProgressVisible = false
        override val progress = 0f
    }

    data class FAILED(val splitInstallError: SplitInstallError) : DocumentSheetUiState {
        override val isButtonEnabled = false
        override val buttonText = "INSTALLED"
        override val isProgressVisible = false
        override val progress = 0f
    }

    data object CANCELING : DocumentSheetUiState {
        override val isButtonEnabled = false
        override val buttonText = "CANCELING"
        override val isProgressVisible = true
        override val progress = 0f
    }

    data object CANCELED : DocumentSheetUiState {
        override val isButtonEnabled = true
        override val buttonText = "DOWNLOAD"
        override val isProgressVisible = false
        override val progress = 0f
    }

    data object NONE : DocumentSheetUiState {
        override val isButtonEnabled = true
        override val buttonText = "DOWNLOAD"
        override val isProgressVisible = false
        override val progress = 0f
    }
}

@Composable
internal fun DocumentSheet(
    uiState: DocumentSheetUiState,
    onDownloadClick: () -> Unit,
    contentPadding: PaddingValues,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(contentPadding)
            .padding(ComicTheme.dimension.margin),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            ComicIcons.UndrawResumeFolder,
            contentDescription = null,
            modifier = Modifier
                .sizeIn(maxHeight = 400.dp, maxWidth = 400.dp)
                .fillMaxSize(0.5f),
        )

        Spacer(modifier = Modifier.size(16.dp))

        Text(
            text = stringResource(R.string.tutorial_text_document),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .widthIn(max = 400.dp)
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.size(16.dp))

        Text(
            text = stringResource(R.string.tutorial_text_document_description),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .widthIn(max = 400.dp)
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.size(16.dp))

        if (uiState.isProgressVisible) {
            LinearProgressIndicator(
                progress = uiState::progress,
                modifier = Modifier
                    .widthIn(max = 400.dp)
                    .fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.size(16.dp))

        TextButton(onClick = onDownloadClick, enabled = uiState.isButtonEnabled) {
            Row {
                Icon(ComicIcons.InstallMobile, contentDescription = null)
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))

                Text(text = uiState.buttonText)
            }
        }
    }
}

@PreviewMultiScreen
@Composable
private fun PreviewDocumentSheet() {
    PreviewTheme {
        Surface {
            DocumentSheet(
                uiState = DocumentSheetUiState.INSTALLED,
                onDownloadClick = {},
                contentPadding = PaddingValues()
            )
        }
    }
}
