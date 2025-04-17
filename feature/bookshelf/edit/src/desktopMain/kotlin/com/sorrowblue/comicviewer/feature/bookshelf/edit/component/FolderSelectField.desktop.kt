package com.sorrowblue.comicviewer.feature.bookshelf.edit.component

import io.github.vinceglb.filekit.core.PlatformDirectory

actual fun localUriToDisplayPath(path: String): String {
    return path
}

actual val PlatformDirectory.pathString: String
    get() = file.absolutePath
