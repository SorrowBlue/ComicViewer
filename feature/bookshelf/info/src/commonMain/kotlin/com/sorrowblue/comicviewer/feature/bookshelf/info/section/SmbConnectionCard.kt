package com.sorrowblue.comicviewer.feature.bookshelf.info.section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.domain.model.bookshelf.DeviceStorage
import com.sorrowblue.comicviewer.domain.model.bookshelf.SmbServer
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.file.component.FileInfoCardTitle
import com.sorrowblue.comicviewer.file.component.FileInfoDataText
import com.sorrowblue.comicviewer.file.section.createClipEntry
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.composeicons.LockPerson
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import comicviewer.feature.bookshelf.info.generated.resources.Res
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_label_Id_password
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_label_auth_method
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_label_connection_details
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_label_guest
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_label_host
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_label_path
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_label_port
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_label_storage_details
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun SmbConnectionCard(
    smbServer: SmbServer,
    folder: Folder,
    modifier: Modifier = Modifier,
) {
    OutlinedCard(modifier = modifier) {
        Column(modifier = Modifier.padding(32.dp)) {
            FileInfoCardTitle(
                ComicIcons.Dns,
                title = {
                    Text(stringResource(Res.string.bookshelf_info_label_connection_details))
                },
            )
            Spacer(modifier = Modifier.size(16.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                maxItemsInEachRow = 4,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FileInfoDataText(
                    overlineContent = {
                        Text(
                            stringResource(Res.string.bookshelf_info_label_host),
                        )
                    },
                    headlineContent = { Text(smbServer.host) },
                    modifier = Modifier.weight(1f),
                )
                FileInfoDataText(
                    overlineContent = {
                        Text(
                            stringResource(Res.string.bookshelf_info_label_port),
                        )
                    },
                    headlineContent = { Text(smbServer.port.toString()) },
                    modifier = Modifier.weight(1f),
                )
            }
            Text(
                text = stringResource(Res.string.bookshelf_info_label_path),
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
                        stringResource(Res.string.bookshelf_info_label_auth_method),
                    )
                },
                headlineContent = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = ComicIcons.LockPerson,
                            contentDescription = null,
                            tint = ComicTheme.colorScheme.secondary,
                            modifier = Modifier.size(ButtonDefaults.IconSize),
                        )
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text(
                            when (smbServer.auth) {
                                SmbServer.Auth.Guest -> stringResource(
                                    Res.string.bookshelf_info_label_guest,
                                )

                                is SmbServer.Auth.UsernamePassword -> stringResource(
                                    Res.string.bookshelf_info_label_Id_password,
                                )
                            },
                        )
                    }
                },
            )
            FileInfoDataText(
                overlineContent = { Text("FILES") },
                headlineContent = { Text(smbServer.fileCount.toString()) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
internal fun DeviceStorageCard(
    deviceStorage: DeviceStorage,
    folder: Folder,
    modifier: Modifier = Modifier,
) {
    OutlinedCard(modifier = modifier) {
        Column(modifier = Modifier.padding(32.dp)) {
            FileInfoCardTitle(
                ComicIcons.SdStorage,
                title = {
                    Text(stringResource(Res.string.bookshelf_info_label_storage_details))
                },
            )
            Spacer(modifier = Modifier.size(16.dp))
            Text(
                text = stringResource(Res.string.bookshelf_info_label_path),
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
                overlineContent = { Text("FILES") },
                headlineContent = { Text(deviceStorage.fileCount.toString()) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}
