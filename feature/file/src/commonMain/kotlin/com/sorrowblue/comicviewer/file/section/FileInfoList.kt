package com.sorrowblue.comicviewer.file.section

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import com.sorrowblue.comicviewer.domain.model.extension
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.IFolder
import com.sorrowblue.comicviewer.framework.ui.adaptive.ExtraPaneScaffoldDefaults
import comicviewer.feature.file.generated.resources.Res
import comicviewer.feature.file.generated.resources.file_label_folder
import comicviewer.feature.file.generated.resources.file_label_last_read_time
import comicviewer.feature.file.generated.resources.file_label_modified_date
import comicviewer.feature.file.generated.resources.file_label_page_count
import comicviewer.feature.file.generated.resources.file_label_path
import comicviewer.feature.file.generated.resources.file_label_size
import comicviewer.feature.file.generated.resources.file_label_type
import comicviewer.feature.file.generated.resources.file_text_page_count
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

expect fun String.createClipEntry(): ClipEntry

@Composable
internal fun FileInfoList(file: File, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        ListItemDefaults.colors(containerColor = Color.Transparent)
        val clipboardManager = LocalClipboard.current
        val scope = rememberCoroutineScope()
        FileInfoListItem(
            overlineContent = { Text(text = stringResource(Res.string.file_label_path)) },
            headlineContent = { Text(text = file.path) },
            modifier = Modifier.combinedClickable(onLongClick = {
                scope.launch {
                    clipboardManager.setClipEntry(file.path.createClipEntry())
                }
            }, onClick = {}),
        )
        FileInfoListItem(
            overlineContent = { Text(text = stringResource(Res.string.file_label_type)) },
            headlineContent = { Text(text = if (file is IFolder) stringResource(Res.string.file_label_folder) else file.name.extension) },
        )
        FileInfoListItem(
            overlineContent = { Text(text = stringResource(Res.string.file_label_size)) },
            headlineContent = { Text(text = file.size.asFileSize) },
        )
        FileInfoListItem(
            overlineContent = { Text(text = stringResource(Res.string.file_label_modified_date)) },
            headlineContent = { Text(text = file.lastModifier.asDateTime) },
        )
        if (file is Book) {
            FileInfoListItem(
                overlineContent = { Text(text = stringResource(Res.string.file_label_page_count)) },
                headlineContent = {
                    Text(
                        text = pluralStringResource(
                            Res.plurals.file_text_page_count,
                            file.totalPageCount,
                            file.totalPageCount,
                        ),
                    )
                },
            )
            FileInfoListItem(
                overlineContent = { Text(text = stringResource(Res.string.file_label_last_read_time)) },
                headlineContent = { Text(text = file.lastReadTime.asDateTime) },
            )
        }
    }
}

val Long.asFileSize: String
    get() {
        var a = this / Byte
        return if (a < Byte) {
            "${a.format()} KB"
        } else {
            a /= Byte
            if (a < Byte) {
                "${a.format()} MB"
            } else {
                a /= Byte
                "${a.format()} GB"
            }
        }
    }

@Composable
private fun FileInfoListItem(
    overlineContent: @Composable () -> Unit,
    headlineContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    val transparentColor = ListItemDefaults.colors(containerColor = Color.Transparent)
    ListItem(
        colors = transparentColor,
        overlineContent = overlineContent,
        headlineContent = headlineContent,
        modifier = modifier
            .padding(horizontal = ExtraPaneScaffoldDefaults.HorizontalPadding),
    )
}

private const val Byte = 1024f

private fun Float.format(decimalPlaces: Int = 2): String {
    val multiplier = 10.0.pow(decimalPlaces.toDouble())
    return ((this * multiplier).roundToInt() / multiplier).toString()
}

@OptIn(ExperimentalTime::class)
val Long.asDateTime: String
    get() = Instant
        .fromEpochMilliseconds(this)
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .format(
            LocalDateTime.Format {
                date(LocalDate.Formats.ISO)
                char(' ')
                time(LocalTime.Formats.ISO)
            },
        )
