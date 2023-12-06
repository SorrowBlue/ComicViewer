package com.sorrowblue.comicviewer.feature.tutorial.section

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.domain.model.SupportExtension
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawFileBundle
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.PreviewComic
import com.sorrowblue.comicviewer.framework.ui.material3.PreviewTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun ArchiveSheet(
    contentPadding: PaddingValues,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .safeDrawingPadding()
            .padding(contentPadding)
            .padding(ComicTheme.dimension.margin),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            imageVector = ComicIcons.UndrawFileBundle,
            contentDescription = null,
            modifier = Modifier
                .sizeIn(maxHeight = 400.dp, maxWidth = 400.dp)
                .fillMaxSize(0.5f),
        )
        Spacer(modifier = Modifier.size(16.dp))

        Text(
            text = "Support Archive Compress File",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .widthIn(max = 400.dp)
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.size(16.dp))

        Text(
            text = "Supports viewing of archived and compressed files. Supported extensions are zip, rar, 7z, etc. Files with passwords are also supported.",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .widthIn(max = 400.dp)
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.size(16.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .widthIn(max = 400.dp)
                .fillMaxWidth()
        ) {
            SupportExtension.Archive.entries.forEach {
                AssistChip(
                    onClick = {},
                    label = {
                        Text(text = ".${it.extension}")
                    }
                )
            }
        }
    }
}

@PreviewComic
@Composable
private fun PreviewArchiveSheet() {
    PreviewTheme {
        Surface {
            ArchiveSheet(
                contentPadding = PaddingValues()
            )
        }
    }
}
