package com.sorrowblue.comicviewer.file.section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.file.component.FileInfoCardTitle
import com.sorrowblue.comicviewer.file.component.FileInfoDataText
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import comicviewer.feature.file.generated.resources.Res
import comicviewer.feature.file.generated.resources.file_label_modified_date
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun FolderIdentity(folder: Folder, modifier: Modifier = Modifier) {
    OutlinedCard(modifier = modifier) {
        Column(modifier = Modifier.padding(32.dp)) {
            FileInfoCardTitle(
                ComicIcons.Book,
                title = {
                    Text("Folder Identity")
                },
            )
            Spacer(modifier = Modifier.size(16.dp))
            Text(
                text = "FULL PATH",
                style = ComicTheme.typography.labelSmall,
            )
            Spacer(modifier = Modifier.size(8.dp))
            OutlinedTextField(
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = ComicTheme.colorScheme.surfaceContainerHighest,
                    unfocusedBorderColor = Color.Transparent,
                ),
                value = folder.path,
                readOnly = true,
                onValueChange = {},
                singleLine = true,
                trailingIcon = {
                    val clipboardManager = LocalClipboard.current
                    val scope = rememberCoroutineScope()
                    IconButton(onClick = {
                        scope.launch {
                            clipboardManager.setClipEntry(folder.path.createClipEntry())
                        }
                    }) {
                        Icon(ComicIcons.ContentCopy, null)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.size(16.dp))
            FileInfoDataText(
                overlineContent = {
                    Text(
                        text = stringResource(Res.string.file_label_modified_date),
                    )
                },
                headlineContent = { Text(text = folder.lastModifier.asDateTime) },
            )
            Spacer(modifier = Modifier.size(8.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                maxItemsInEachRow = 4,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FileInfoDataText(
                    overlineContent = { Text(text = "FILES") },
                    headlineContent = { Text(text = "${folder.count} files") },
                    modifier = Modifier.weight(1f),
                )
                FileInfoDataText(
                    overlineContent = { Text(text = "SIZE") },
                    headlineContent = {
                        Text(
                            text = if (0 < folder.size) folder.size.asFileSize else "-",
                        )
                    },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}
