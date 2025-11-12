package com.sorrowblue.comicviewer.feature.tutorial.section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.domain.model.SupportExtension
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawResumeFolder
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewMultiplatform
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import comicviewer.feature.tutorial.generated.resources.Res
import comicviewer.feature.tutorial.generated.resources.tutorial_text_document
import comicviewer.feature.tutorial.generated.resources.tutorial_text_document_description
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun DocumentSheet(contentPadding: PaddingValues) {
    TutorialSheet(
        image = ComicIcons.UndrawResumeFolder,
        contentDescription = null,
        title = {
            Text(text = stringResource(Res.string.tutorial_text_document))
        },
        description = {
            Text(
                text = stringResource(Res.string.tutorial_text_document_description),
                modifier = Modifier.widthIn(max = 420.dp),
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.size(ComicTheme.dimension.margin))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(
                    ComicTheme.dimension.padding,
                    alignment = Alignment.CenterHorizontally,
                ),
                verticalArrangement = Arrangement.spacedBy(
                    0.dp,
                ),
                modifier = Modifier.widthIn(max = 420.dp),
            ) {
                SupportExtension.Document.entries.forEach {
                    AssistChip(onClick = {}, label = { Text(text = ".${it.extension}") })
                }
            }
            Spacer(modifier = Modifier.size(ComicTheme.dimension.padding))
            DocumentSheetOption(
                modifier = Modifier.widthIn(max = 420.dp),
            )
        },
        contentPadding = contentPadding,
    )
}

@PreviewMultiplatform
@Composable
private fun DirectionSheetPreview() {
    PreviewTheme {
        Surface {
            DocumentSheet(contentPadding = PaddingValues())
        }
    }
}
