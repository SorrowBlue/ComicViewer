package com.sorrowblue.comicviewer.feature.bookshelf.edit.component

import androidx.core.uri.Uri
import io.github.vinceglb.filekit.core.PlatformDirectory
import kotlinx.cinterop.ExperimentalForeignApi
import logcat.logcat
import net.thauvin.erik.urlencoder.UrlEncoderUtil
import platform.Foundation.NSURL

actual fun localUriToDisplayPath(uri: Uri): String {
    return UrlEncoderUtil.decode(NSURL(fileURLWithPath = uri.toString()).lastPathComponent.orEmpty())
}

@OptIn(ExperimentalForeignApi::class)
actual fun PlatformDirectory.displayPath(): String {
    logcat { """
        path=$path
        nsUrl=$nsUrl
        fileURL=${nsUrl.fileURL}
        hasDirectoryPath=${nsUrl.hasDirectoryPath}
        checkResourceIsReachableAndReturnError=${nsUrl.checkResourceIsReachableAndReturnError(null)}
    """.trimIndent() }
    var url = NSURL.fileURLWithPath(path = path!!)
    logcat { """
        path=${url.path}
        nsUrl=$url
        fileURL=${url.fileURL}
        hasDirectoryPath=${url.hasDirectoryPath}
        checkResourceIsReachableAndReturnError=${url.checkResourceIsReachableAndReturnError(null)}
    """.trimIndent() }
    url = NSURL.URLWithString(URLString = path!!)!!
    logcat { """
        path=${url.path}
        absoluteString=${url.absoluteString}
        nsUrl=$url
        fileURL=${url.fileURL}
        hasDirectoryPath=${url.hasDirectoryPath}
        checkResourceIsReachableAndReturnError=${url.checkResourceIsReachableAndReturnError(null)}
    """.trimIndent() }
    return ""
}