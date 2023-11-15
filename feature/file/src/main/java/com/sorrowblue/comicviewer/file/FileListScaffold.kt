package com.sorrowblue.comicviewer.file

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.framework.ui.responsive.SideSheetValueState
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun rememberSideSheetFileState(
    initialValue: File? = null,
) = rememberSaveable(
    saver = Saver(
        save = { initialValue },
        restore = { savedValue -> SideSheetValueState(savedValue) }
    )
) {
    SideSheetValueState(initialValue)
}

val Long.asFileSize: String
    get() {
        var a = this / 1024f
        return if (a < 1024) {
            "%.2f".format(a) + " KB"
        } else {
            a /= 1024f
            if (a < 1024) {
                "%.2f".format(a) + " MB"
            } else {
                a /= 1024f
                "%.2f".format(a) + " GB"
            }
        }
    }

val Long.asDateTime: String
    get() = Instant.ofEpochMilli(this)
        .atZone(ZoneOffset.systemDefault())
        .format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT))

fun lastReadPage(lastReadPage: Int, maxPage: Int) = "$lastReadPage/$maxPage pages"
