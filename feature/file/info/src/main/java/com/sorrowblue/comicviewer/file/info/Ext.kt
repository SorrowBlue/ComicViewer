package com.sorrowblue.comicviewer.file.info

import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

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

fun lastReadPage(lastReadPage: Int, maxPage: Int) = "${lastReadPage}/${maxPage} pages"
