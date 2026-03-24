package com.sorrowblue.comicviewer.file.section

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.datetime.offsetAt
import kotlinx.datetime.toLocalDateTime

expect fun String.createClipEntry(): ClipEntry

@Composable
internal fun FileInfoList(file: File, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        if (file is Book) {
            BookIdentity(book = file)
            Spacer(Modifier.size(16.dp))
            ReadingInsight(book = file)
        } else if (file is Folder) {
            FolderIdentity(folder = file)
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

@OptIn(ExperimentalTime::class)
val Long.asDate: String
    get() = Instant
        .fromEpochMilliseconds(this)
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .format(
            LocalDateTime.Format {
                date(LocalDate.Formats.ISO)
            },
        )

@OptIn(ExperimentalTime::class)
val Long.asTime: String
    get() {
        val systemZone = TimeZone.currentSystemDefault()
        val instant = Instant.fromEpochMilliseconds(this)
        return instant.toLocalDateTime(TimeZone.currentSystemDefault())
            .format(
                LocalDateTime.Format {
                    time(
                        LocalTime.Format {
                            hour()
                            char(':')
                            minute()
                        },
                    )
                },
            ) + " ${systemZone.offsetAt(instant)}"
    }
