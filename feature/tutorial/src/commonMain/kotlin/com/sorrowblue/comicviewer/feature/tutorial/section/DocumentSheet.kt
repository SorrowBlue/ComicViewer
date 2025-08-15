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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawResumeFolder
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import comicviewer.feature.tutorial.generated.resources.Res
import comicviewer.feature.tutorial.generated.resources.tutorial_text_document
import comicviewer.feature.tutorial.generated.resources.tutorial_text_document_btn_download
import comicviewer.feature.tutorial.generated.resources.tutorial_text_document_description
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun DocumentSheet(
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
            text = stringResource(Res.string.tutorial_text_document),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .widthIn(max = 400.dp)
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.size(16.dp))

        Text(
            text = stringResource(Res.string.tutorial_text_document_description),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .widthIn(max = 400.dp)
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.size(16.dp))

        Spacer(modifier = Modifier.size(16.dp))

        TextButton(onClick = onDownloadClick) {
            Row {
                Icon(ComicIcons.InstallMobile, contentDescription = null)
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                Text(text = stringResource(Res.string.tutorial_text_document_btn_download))
            }
        }
    }
}
