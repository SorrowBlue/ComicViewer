package com.sorrowblue.comicviewer.folder.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.PlainTooltipBox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.domain.entity.file.BookFile
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.file.R
import com.sorrowblue.comicviewer.file.info.Converter
import com.sorrowblue.comicviewer.file.info.Converter.extension
import com.sorrowblue.comicviewer.framework.compose.AppMaterialTheme
import com.sorrowblue.comicviewer.framework.compose.placeholder.debugPlaceholder
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FileInfoBottomSheet(
    file: File,
    onDismissRequest: () -> Unit,
    onAddReadLaterRequest: (File) -> Unit,
    navController: NavHostController = rememberNavController()
) {
    ModalBottomSheet(onDismissRequest = onDismissRequest) {
        Column(
            modifier = Modifier.padding(horizontal = AppMaterialTheme.dimens.margin),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = file,
                contentDescription = when (file) {
                    is Book -> "book thumbnail"
                    is Folder -> "folder thumbnail"
                },
                modifier = Modifier.size(150.dp),
                placeholder = debugPlaceholder()
            )
            Text(
                text = file.name,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = file.parent,
                modifier = Modifier.padding(top = AppMaterialTheme.dimens.spacer),
                style = MaterialTheme.typography.labelSmall
            )
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = AppMaterialTheme.dimens.spacer),
                horizontalArrangement = Arrangement.spacedBy(AppMaterialTheme.dimens.spacer)
            ) {
                PlainTooltipBox(tooltip = {
                    Text("ファイルの拡張子")
                }) {
                    AssistChip(
                        onClick = {},
                        label = { Text(file.name.extension().orEmpty()) },
                        modifier = Modifier.tooltipAnchor()
                    )
                }
                AssistChip(
                    onClick = {},
                    label = { Text(Converter.fileSize(file.size)) })
                AssistChip(
                    onClick = {},
                    label = { Text(Converter.dateTime(file.lastModifier)) })
                if (file is Book) {
                    AssistChip(
                        onClick = {},
                        label = {
                            Text(Converter.lastReadPage(file.lastPageRead, file.totalPageCount))
                        }
                    )
                    AssistChip(
                        onClick = {},
                        label = {
                            Text(
                                stringResource(
                                    id = R.string.file_info_label_last_read_time,
                                    Converter.dateTime(file.lastReadTime)
                                )
                            )
                        }
                    )
                }
            }
            FlowRow(
                Modifier
                    .fillMaxWidth()
                    .padding(top = AppMaterialTheme.dimens.spacer),
                horizontalArrangement = Arrangement.spacedBy(AppMaterialTheme.dimens.spacer)
            ) {
                FilledTonalButton(onClick = { onAddReadLaterRequest(file) }) {
                    Text(stringResource(id = R.string.file_info_label_add_read_later))
                }
                FilledTonalButton(onClick = {
                    navController.navigate("comicviewer://comicviewer.sorrowblue.com/favorite/add?serverId=${file.bookshelfId.value}&filePath=${file.base64Path()}".toUri())
                }) {
                    Text(stringResource(id = R.string.file_info_label_add_favourites))
                }
                FilledTonalButton(onClick = {
                    navController.navigate("comicviewer://comicviewer.sorrowblue.com/folder?bookshelf_id=${file.bookshelfId.value}&path=${file.path}".toUri())
                }) {
                    Text(stringResource(id = R.string.file_info_label_open_folder))
                }
            }
        }
    }
}

private class FilePreviewParameterProvider : PreviewParameterProvider<File> {
    override val values = sequenceOf(
        BookFile(
            BookshelfId(Random.nextInt()),
            "Book name.zip",
            "/books/aadwd/",
            "/books/aadwd/Book name.zip",
            52244225,
            44535444,
            "xxxxxxxxxxxxxxxx",
            23,
            124,
            212351243
        ),
        Folder(
            BookshelfId(Random.nextInt()),
            "Book name.zip",
            "/books/aadwd/",
            "/books/aadwd/Book name.zip",
            52244225,
            44535444,
            count = 50,
        )
    )
}

@Preview
@Composable
fun PreviewFileInfoBottomSheet(@PreviewParameter(FilePreviewParameterProvider::class) file: File) {
    AppMaterialTheme {
        FileInfoBottomSheet(
            file = file,
            onDismissRequest = { /*TODO*/ },
            onAddReadLaterRequest = {}
        )
    }
}
