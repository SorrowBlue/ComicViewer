package com.sorrowblue.comicviewer.file.info

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Book
import androidx.compose.material.icons.twotone.Extension
import androidx.compose.material.icons.twotone.Favorite
import androidx.compose.material.icons.twotone.FolderOpen
import androidx.compose.material.icons.twotone.History
import androidx.compose.material.icons.twotone.Update
import androidx.compose.material.icons.twotone.WatchLater
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.domain.entity.file.BookFile
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.domain.entity.server.BookshelfId
import com.sorrowblue.comicviewer.domain.usecase.GetLibraryInfoError
import com.sorrowblue.comicviewer.framework.Result

@Composable
fun debugPlaceholder(@DrawableRes debugPreview: Int) =
    if (LocalInspectionMode.current) {
        painterResource(id = debugPreview)
    } else {
        null
    }

@Composable
fun Knob(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .padding(top = 22.dp, bottom = 22.dp)
            .background(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                shape = RoundedCornerShape(size = 8.dp)
            )
            .alpha(0.4f)
            .width(32.dp)
            .height(4.dp)
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FileInfoScreen(
    result: Result<File, GetLibraryInfoError>?,
    onClickReadLater: (File) -> Unit,
    onClickFavorite: (File) -> Unit,
    onClickOpenParent: (File) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(horizontal = 16.dp)
    ) {
        Knob()
        when (result) {
            null -> {
            }
            is Result.Error ->{
                Text(
                    text = "ファイルが見つかりませんでした。",
                    style = MaterialTheme.typography.titleLarge.copy(MaterialTheme.colorScheme.onSurface)
                )
            }
            is Result.Exception ->{
                Text(
                    text = "エラーが発生しました。",
                    style = MaterialTheme.typography.titleLarge.copy(MaterialTheme.colorScheme.onSurface)
                )
            }
            is Result.Success -> {
                val file = result.data
                Row(Modifier.fillMaxWidth()) {
                    when (file) {
                        is Book -> AsyncImage(
                            model = file,
                            placeholder = debugPlaceholder(com.sorrowblue.comicviewer.framework.resource.R.drawable.ic_undraw_bookshelves_re_lxoy),
                            contentDescription = "本のサムネイル",
                            modifier = Modifier
                                .size(150.dp)
                        )

                        is Folder -> Box {
                            val (w, h) = with(LocalDensity.current) {
                                150.dp.roundToPx() to 150.dp.roundToPx()
                            }
                            AsyncImage(
                                model = file,
                                contentDescription = "フォルダのサムネイル",
                                modifier = Modifier
                                    .size(150.dp)
                                    .padding(
                                        start = 12.dp,
                                        top = 16.dp,
                                        end = 12.dp,
                                        bottom = 12.dp
                                    )
                            )
                        }
                    }
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp)
                    ) {
                        Text(
                            text = file.name,
                            style = MaterialTheme.typography.titleLarge.copy(MaterialTheme.colorScheme.onSurface)
                        )
                        Text(
                            text = file.path,
                            style = MaterialTheme.typography.labelLarge.copy(MaterialTheme.colorScheme.onSurface)
                        )
                    }
                }

                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    mainAxisAlignment = FlowMainAxisAlignment.Center,
                    mainAxisSpacing = 16.dp,
                    crossAxisSpacing = 0.dp
                ) {
                    if (file is Book) {
                        AssistChip(
                            onClick = {},
                            label = {
                                Text(
                                    file.path.substringAfterLast('.').lowercase()
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.TwoTone.Extension,
                                    null,
                                    Modifier.size(AssistChipDefaults.IconSize)
                                )
                            },
                        )
                    }
                    AssistChip(
                        onClick = {},
                        label = { Text(Converter.fileSize(file.size)) },
                        leadingIcon = {
                            Icon(
                                Icons.TwoTone.Book,
                                null,
                                Modifier.size(AssistChipDefaults.IconSize)
                            )
                        },
                    )
                    AssistChip(
                        onClick = {},
                        label = { Text(Converter.dateTime(file.lastModifier)) },
                        leadingIcon = {
                            Icon(
                                Icons.TwoTone.Update,
                                null,
                                Modifier.size(AssistChipDefaults.IconSize)
                            )
                        },
                    )
                    if (file is Book) {
                        AssistChip(
                            onClick = {},
                            label = {
                                Text("${file.lastPageRead}/${file.totalPageCount} pages")
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.TwoTone.History,
                                    null,
                                    Modifier.size(AssistChipDefaults.IconSize)
                                )
                            },
                        )
                        AssistChip(
                            onClick = {},
                            label = { Text(Converter.dateTime(file.lastReadTime) + "に読んだ") },
                            leadingIcon = {
                                Icon(
                                    Icons.TwoTone.History,
                                    null,
                                    Modifier.size(AssistChipDefaults.IconSize)
                                )
                            },
                        )
                    }
                }
                FlowRow(
                    mainAxisSpacing = 8.dp, crossAxisSpacing = 0.dp, modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 8.dp)
                ) {
                    FilledTonalButtonIcon({
                        onClickReadLater(file)
                    }, "Read later", Icons.TwoTone.WatchLater, null)
                    FilledTonalButtonIcon({
                        onClickFavorite(file)
                    }, "Save to favorites", Icons.TwoTone.Favorite, null)
                    FilledTonalButtonIcon({
                        onClickOpenParent(file)
                    }, "Open folder", Icons.TwoTone.FolderOpen, null)
                }
            }
        }
    }
}

@Preview(uiMode = UI_MODE_NIGHT_NO, device = Devices.PIXEL_4, showBackground = true)
@Composable
fun PreviewBookInfoScreen() {
    SampleTheme {
        FileInfoScreen(
            result = Result.Success(
                BookFile(
                    BookshelfId(0),
                    "本の名前",
                    "本の親パス",
                    "本のパス",
                    4643241,
                    444444444444444,
                    "",
                    199,
                    255,
                    444444444444444,
                )
            ), {}, {}
        ) {}
    }
}

@Preview(uiMode = UI_MODE_NIGHT_NO, device = Devices.PIXEL_4, showBackground = true)
@Composable
fun PreviewFolderInfoScreen() {
    SampleTheme {
        FileInfoScreen(
            result = Result.Success(
                Folder(
                    BookshelfId(0),
                    "本の名前",
                    "本の親パス",
                    "本のパス",
                    4643241,
                    444444444444444,
                )
            ), {}, {}
        ) {}
    }
}

@Preview(uiMode = UI_MODE_NIGHT_NO, device = Devices.PIXEL_4, showBackground = true)
@Composable
fun PreviewErrorInfoScreen() {
    SampleTheme {
        FileInfoScreen(
            result = Result.Error(GetLibraryInfoError.NOT_FOUND), {}, {}
        ) {}
    }
}
