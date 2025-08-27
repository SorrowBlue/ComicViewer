package com.sorrowblue.comicviewer.feature.tutorial.section

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawResumeFolder
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import comicviewer.feature.tutorial.generated.resources.Res
import comicviewer.feature.tutorial.generated.resources.tutorial_text_document
import comicviewer.feature.tutorial.generated.resources.tutorial_text_document_description
import org.jetbrains.compose.resources.stringResource

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
                modifier = Modifier.widthIn(max = 400.dp),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.size(ComicTheme.dimension.padding))
            DocumentSheetOption()
        },
        contentPadding = contentPadding
    )
}
