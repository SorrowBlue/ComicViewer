package com.sorrowblue.comicviewer.feature.bookshelf.edit.component

import androidx.core.net.toUri
import io.github.vinceglb.filekit.core.PlatformDirectory

actual fun localUriToDisplayPath(path: String): String {
    return path.toUri().pathSegments?.lastOrNull()?.split(":")?.lastOrNull()
        .orEmpty()
}

actual val PlatformDirectory.pathString: String
    get() = uri.toString()
