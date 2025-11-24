package com.sorrowblue.comicviewer.feature.bookshelf.info.section

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.uri.UriUtils
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.InternalStorage
import com.sorrowblue.comicviewer.domain.model.bookshelf.ShareContents
import com.sorrowblue.comicviewer.domain.model.bookshelf.SmbServer
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.ExtraPaneScaffoldDefaults
import comicviewer.feature.bookshelf.info.generated.resources.Res
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_label_Id_password
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_label_auth_method
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_label_bookshelf_type
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_label_display_name
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_label_guest
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_label_host
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_label_internal_storage
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_label_path
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_label_port
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_label_smb
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun BookshelfInfo(bookshelf: Bookshelf, folder: Folder, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        ListItemDefaults.colors(containerColor = Color.Transparent)
        BookshelfInfoListItem(
            overlineContent = {
                Text(
                    text = stringResource(Res.string.bookshelf_info_label_bookshelf_type),
                )
            },
            headlineContent = { Text(text = bookshelf.source()) },
        )
        BookshelfInfoListItem(
            overlineContent = {
                Text(
                    text = stringResource(Res.string.bookshelf_info_label_display_name),
                )
            },
            headlineContent = { Text(text = bookshelf.displayName) },
        )
        when (bookshelf) {
            is InternalStorage -> {
                BookshelfInfoListItem(
                    overlineContent = {
                        Text(
                            text = stringResource(Res.string.bookshelf_info_label_path),
                        )
                    },
                    headlineContent = {
                        Text(
                            text = UriUtils
                                .parse(folder.path)
                                .getPathSegments()
                                .lastOrNull()
                                ?.split(":")
                                ?.lastOrNull()
                                .orEmpty(),
                        )
                    },
                )
            }

            is SmbServer -> {
                BookshelfInfoListItem(
                    overlineContent = {
                        Text(
                            text = stringResource(Res.string.bookshelf_info_label_host),
                        )
                    },
                    headlineContent = { Text(text = bookshelf.host) },
                )
                BookshelfInfoListItem(
                    overlineContent = {
                        Text(
                            text = stringResource(Res.string.bookshelf_info_label_port),
                        )
                    },
                    headlineContent = { Text(text = bookshelf.port.toString()) },
                )
                BookshelfInfoListItem(
                    overlineContent = {
                        Text(
                            text = stringResource(Res.string.bookshelf_info_label_path),
                        )
                    },
                    headlineContent = { Text(text = folder.path) },
                )
                when (bookshelf.auth) {
                    SmbServer.Auth.Guest -> {
                        BookshelfInfoListItem(
                            overlineContent = {
                                Text(
                                    text = stringResource(
                                        Res.string.bookshelf_info_label_auth_method,
                                    ),
                                )
                            },
                            headlineContent = {
                                Text(
                                    text = stringResource(Res.string.bookshelf_info_label_guest),
                                )
                            },
                        )
                    }

                    is SmbServer.Auth.UsernamePassword -> {
                        BookshelfInfoListItem(
                            overlineContent = {
                                Text(
                                    text = stringResource(
                                        Res.string.bookshelf_info_label_auth_method,
                                    ),
                                )
                            },
                            headlineContent = {
                                Text(
                                    text = stringResource(
                                        Res.string.bookshelf_info_label_Id_password,
                                    ),
                                )
                            },
                        )
                    }
                }
            }

            ShareContents -> {
            }
        }
    }
}

@Composable
private fun BookshelfInfoListItem(
    overlineContent: @Composable () -> Unit,
    headlineContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    ListItem(
        colors = colors,
        overlineContent = overlineContent,
        headlineContent = headlineContent,
        modifier = modifier
            .padding(horizontal = ExtraPaneScaffoldDefaults.HorizontalPadding),
    )
}

@Composable
private fun Bookshelf?.source() = when (this) {
    is InternalStorage -> stringResource(Res.string.bookshelf_info_label_internal_storage)
    is SmbServer -> stringResource(Res.string.bookshelf_info_label_smb)
    else -> ""
}
