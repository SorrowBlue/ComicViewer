package com.sorrowblue.comicviewer.feature.bookshelf.info.section

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.InternalStorage
import com.sorrowblue.comicviewer.domain.model.bookshelf.ShareContents
import com.sorrowblue.comicviewer.domain.model.bookshelf.SmbServer
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.bookshelf.info.R

@Composable
internal fun BookshelfInfo(
    bookshelf: Bookshelf,
    folder: Folder,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        val colors = ListItemDefaults.colors(containerColor = Color.Transparent)
        ListItem(
            colors = colors,
            overlineContent = { Text(text = stringResource(R.string.bookshelf_info_label_bookshelf_type)) },
            headlineContent = { Text(text = stringResource(id = bookshelf.source())) },
            modifier = Modifier
                .combinedClickable(onLongClick = {}) { }
                .padding(horizontal = 12.dp)
        )
        ListItem(
            colors = colors,
            overlineContent = { Text(text = stringResource(id = R.string.bookshelf_info_label_display_name)) },
            headlineContent = { Text(text = bookshelf.displayName) },
            modifier = Modifier
                .combinedClickable(onLongClick = {}) { }
                .padding(horizontal = 12.dp)
        )
        when (bookshelf) {
            is InternalStorage -> {
                ListItem(
                    colors = colors,
                    overlineContent = { Text(text = stringResource(id = R.string.bookshelf_info_label_path)) },
                    headlineContent = {
                        Text(
                            text = folder.path.toUri().lastPathSegment?.split(":")?.lastOrNull()
                                .orEmpty()
                        )
                    },
                    modifier = Modifier
                        .combinedClickable(onLongClick = {}) { }
                        .padding(horizontal = 12.dp)
                )
            }

            is SmbServer -> {
                ListItem(
                    colors = colors,
                    overlineContent = { Text(text = stringResource(id = R.string.bookshelf_info_label_host)) },
                    headlineContent = { Text(text = bookshelf.host) },
                    modifier = Modifier
                        .combinedClickable(onLongClick = {}) { }
                        .padding(horizontal = 12.dp)
                )
                ListItem(
                    colors = colors,
                    overlineContent = { Text(text = stringResource(id = R.string.bookshelf_info_label_port)) },
                    headlineContent = { Text(text = bookshelf.port.toString()) },
                    modifier = Modifier
                        .combinedClickable(onLongClick = {}) { }
                        .padding(horizontal = 12.dp)
                )
                ListItem(
                    colors = colors,
                    overlineContent = { Text(text = stringResource(id = R.string.bookshelf_info_label_path)) },
                    headlineContent = { Text(text = folder.path) },
                    modifier = Modifier
                        .combinedClickable(onLongClick = {}) { }
                        .padding(horizontal = 12.dp)
                )
                when (bookshelf.auth) {
                    SmbServer.Auth.Guest -> {
                        ListItem(
                            colors = colors,
                            overlineContent = {
                                Text(
                                    text = stringResource(R.string.bookshelf_info_label_auth_method)
                                )
                            },
                            headlineContent = { Text(text = stringResource(R.string.bookshelf_info_label_guest)) },
                            modifier = Modifier
                                .combinedClickable(onLongClick = {}) { }
                                .padding(horizontal = 12.dp)
                        )
                    }

                    is SmbServer.Auth.UsernamePassword -> {
                        ListItem(
                            colors = colors,
                            overlineContent = {
                                Text(
                                    text = stringResource(R.string.bookshelf_info_label_auth_method)
                                )
                            },
                            headlineContent = {
                                Text(
                                    text = stringResource(R.string.bookshelf_info_label_Id_password)
                                )
                            },
                            modifier = Modifier
                                .combinedClickable(onLongClick = {}) { }
                                .padding(horizontal = 12.dp)
                        )
                    }
                }
            }

            ShareContents -> {
            }
        }
    }
}

private fun Bookshelf?.source() = when (this) {
    is InternalStorage -> R.string.bookshelf_info_label_internal_storage
    is SmbServer -> R.string.bookshelf_info_label_smb
    null -> android.R.string.unknownName
    ShareContents -> 0
}
