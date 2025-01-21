package com.sorrowblue.comicviewer.feature.tutorial.section

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.movableContentWithReceiverOf
import androidx.compose.runtime.remember
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
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun ArchiveSheet(contentPadding: PaddingValues) {
    val icon = remember {
        movableContentWithReceiverOf<ColumnScope> {
            Image(
                imageVector = ComicIcons.UndrawFileBundle,
                contentDescription = null,
                modifier = Modifier.size(160.dp)
            )
            Spacer(modifier = Modifier.size(ComicTheme.dimension.padding))
            Text(
                text = stringResource(Res.string.tutorial_text_archive),
                style = MaterialTheme.typography.titleLarge,
            )
        }
    }
    val message = remember {
        movableContentWithReceiverOf<ColumnScope> {
            Text(
                text = stringResource(Res.string.tutorial_text_archive_description),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.size(ComicTheme.dimension.padding))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(
                    ComicTheme.dimension.padding,
                    alignment = Alignment.CenterHorizontally
                ),
                verticalArrangement = Arrangement.spacedBy(
                    0.dp,
                ),
                modifier = Modifier.fillMaxWidth(),
            ) {
                SupportExtension.Archive.entries.forEach {
                    AssistChip(onClick = {}, label = { Text(text = ".${it.extension}") })
                }
            }
        }
    }
    BoxWithConstraints {
        if (maxWidth < maxHeight) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(contentPadding)
                    .padding(ComicTheme.dimension.margin),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    icon()
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    message()
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .padding(ComicTheme.dimension.margin),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    icon()
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(modifier = Modifier.weight(0.8f))
                    message()
                    Spacer(modifier = Modifier.weight(0.2f))
                }
            }
        }
    }
}
