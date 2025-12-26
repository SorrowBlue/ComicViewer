package com.sorrowblue.comicviewer.file.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.domain.model.file.FileAttribute
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import comicviewer.feature.file.generated.resources.Res
import comicviewer.feature.file.generated.resources.file_attribute_archive
import comicviewer.feature.file.generated.resources.file_attribute_compressed
import comicviewer.feature.file.generated.resources.file_attribute_directory
import comicviewer.feature.file.generated.resources.file_attribute_hidden
import comicviewer.feature.file.generated.resources.file_attribute_normal
import comicviewer.feature.file.generated.resources.file_attribute_readonly
import comicviewer.feature.file.generated.resources.file_attribute_shared_read
import comicviewer.feature.file.generated.resources.file_attribute_system
import comicviewer.feature.file.generated.resources.file_attribute_temporary
import comicviewer.feature.file.generated.resources.file_attribute_volume
import comicviewer.feature.file.generated.resources.file_label_attributes
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun FileAttributeChips(fileAttribute: FileAttribute, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(Res.string.file_label_attributes),
            style = ComicTheme.typography.labelSmall,
            modifier = Modifier,
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(ComicTheme.dimension.minPadding),
        ) {
            fileAttribute.let {
                if (it.archive) {
                    AssistChip(onClick = {}, label = { Text(text = stringResource(Res.string.file_attribute_archive)) })
                }
                if (it.compressed) {
                    AssistChip(onClick = {}, label = { Text(text = stringResource(Res.string.file_attribute_compressed)) })
                }
                if (it.hidden) {
                    AssistChip(onClick = {}, label = { Text(text = stringResource(Res.string.file_attribute_hidden)) })
                }
                if (it.normal) {
                    AssistChip(onClick = {}, label = { Text(text = stringResource(Res.string.file_attribute_normal)) })
                }
                if (it.directory) {
                    AssistChip(onClick = {}, label = { Text(text = stringResource(Res.string.file_attribute_directory)) })
                }
                if (it.readonly) {
                    AssistChip(onClick = {}, label = { Text(text = stringResource(Res.string.file_attribute_readonly)) })
                }
                if (it.sharedRead) {
                    AssistChip(onClick = {}, label = { Text(text = stringResource(Res.string.file_attribute_shared_read)) })
                }
                if (it.system) {
                    AssistChip(onClick = {}, label = { Text(text = stringResource(Res.string.file_attribute_system)) })
                }
                if (it.temporary) {
                    AssistChip(onClick = {}, label = { Text(text = stringResource(Res.string.file_attribute_temporary)) })
                }
                if (it.volume) {
                    AssistChip(onClick = {}, label = { Text(text = stringResource(Res.string.file_attribute_volume)) })
                }
            }
        }
    }
}
