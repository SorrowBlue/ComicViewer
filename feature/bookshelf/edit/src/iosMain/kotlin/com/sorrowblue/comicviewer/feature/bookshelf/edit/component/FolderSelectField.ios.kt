package com.sorrowblue.comicviewer.feature.bookshelf.edit.component

import androidx.core.uri.Uri
import net.thauvin.erik.urlencoder.UrlEncoderUtil
import platform.Foundation.NSURL

actual fun localUriToDisplayPath(uri: Uri): String {
    return UrlEncoderUtil.decode(NSURL(fileURLWithPath = uri.toString()).lastPathComponent.orEmpty())
}
