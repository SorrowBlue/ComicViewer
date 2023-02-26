package com.sorrowblue.comicviewer.file.info

import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

object Converter {

    @JvmStatic
    fun String?.extension() = this?.substringAfterLast('.')?.lowercase()

    @JvmStatic
    fun fileSize(fileSize: Long): String {
        var a = fileSize / 1024f
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
    @JvmStatic
    fun dateTime(epochMilli: Long) = Instant.ofEpochMilli(epochMilli)
        .atZone(ZoneOffset.systemDefault())
        .format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM))
    @JvmStatic
    fun lastReadPage(lastReadPage: Int, maxPage: Int) = "${lastReadPage}/${maxPage} pages"
}
