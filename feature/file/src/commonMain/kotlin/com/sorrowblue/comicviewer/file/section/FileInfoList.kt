package com.sorrowblue.comicviewer.file.section

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
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
import comicviewer.feature.file.generated.resources.Res
import comicviewer.feature.file.generated.resources.file_label_modified_date
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
        val transparentColor = ListItemDefaults.colors(containerColor = Color.Transparent)
        val clipboardManager = LocalClipboard.current
        val scope = rememberCoroutineScope()
        ListItem(
            overlineContent = { Text(text = "パス") },
            headlineContent = { Text(text = file.path) },
            colors = transparentColor,
            modifier = Modifier.combinedClickable(onLongClick = {
                scope.launch {
                    clipboardManager.setClipEntry(file.path.createClipEntry())
                }
            }, onClick = {})
        )
        ListItem(
            overlineContent = { Text(text = "種類") },
            headlineContent = { Text(text = if (file is IFolder) "フォルダ" else file.name.extension) },
            colors = transparentColor
        )
        ListItem(
            overlineContent = { Text(text = "サイズ") },
            headlineContent = { Text(text = file.size.asFileSize) },
            colors = transparentColor
        )
        ListItem(
            overlineContent = { Text(text = stringResource(Res.string.file_label_modified_date)) },
            headlineContent = { Text(text = file.lastModifier.asDateTime) },
            colors = transparentColor
        )
        if (file is Book) {
            ListItem(
                overlineContent = { Text(text = "ページ数") },
                headlineContent = {
                    Text(
                        text = pluralStringResource(
                            Res.plurals.file_text_page_count,
                            file.totalPageCount,
                            file.totalPageCount
                        )
                    )
                },
                colors = transparentColor
            )
            ListItem(
                overlineContent = { Text(text = "最後に読んだ日時") },
                headlineContent = { Text(text = file.lastReadTime.asDateTime) },
                colors = transparentColor
            )
        }
    }
}

val Long.asFileSize: String
    get() {
        var a = this / 1024f
        return if (a < 1024) {
            "${a.format()} KB"
        } else {
            a /= 1024f
            if (a < 1024) {
                "${a.format()} MB"
            } else {
                a /= 1024f
                "${a.format()} GB"
            }
        }
    }

private fun Float.format(decimalPlaces: Int = 2): String {
    val multiplier = 10.0.pow(decimalPlaces.toDouble())
    return ((this * multiplier).roundToInt() / multiplier).toString()
}

@OptIn(ExperimentalTime::class)
val Long.asDateTime: String
    get() = Instant.fromEpochMilliseconds(this)
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .format(
            LocalDateTime.Format {
                date(LocalDate.Formats.ISO)
                char(' ')
                time(LocalTime.Formats.ISO)
            }
        )
