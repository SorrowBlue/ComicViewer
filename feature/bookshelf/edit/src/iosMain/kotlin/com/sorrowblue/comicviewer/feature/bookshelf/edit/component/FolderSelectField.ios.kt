package com.sorrowblue.comicviewer.feature.bookshelf.edit.component

import io.github.vinceglb.filekit.core.PlatformDirectory
import platform.Foundation.NSURL

actual fun localUriToDisplayPath(path: String): String {
    return NSURL(string = path).lastPathComponent.orEmpty()
}

actual val PlatformDirectory.pathString: String
    get() = this.nsUrl.absoluteString.orEmpty()
