package com.sorrowblue.comicviewer.feature.tutorial.section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.domain.model.SupportExtension
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawFileBundle
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import comicviewer.feature.tutorial.generated.resources.Res
import comicviewer.feature.tutorial.generated.resources.tutorial_text_archive
import comicviewer.feature.tutorial.generated.resources.tutorial_text_archive_description
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun ArchiveSheet(contentPadding: PaddingValues) {
    TutorialSheet(
        image = ComicIcons.UndrawFileBundle,
        contentDescription = null,
        title = {
            Text(text = stringResource(Res.string.tutorial_text_archive))
        },
        description = {
            Text(
                text = stringResource(Res.string.tutorial_text_archive_description),
                modifier = Modifier.widthIn(max = 420.dp),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.size(ComicTheme.dimension.margin))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(
                    ComicTheme.dimension.padding,
                    alignment = Alignment.CenterHorizontally
                ),
                verticalArrangement = Arrangement.spacedBy(
                    0.dp,
                ),
                modifier = Modifier.widthIn(max = 420.dp),
            ) {
                SupportExtension.Archive.entries.forEach {
                    AssistChip(onClick = {}, label = { Text(text = ".${it.extension}") })
                }
            }
        },
        contentPadding = contentPadding
    )
}
