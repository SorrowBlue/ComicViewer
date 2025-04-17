package com.sorrowblue.comicviewer.feature.bookshelf.edit.component

import io.github.vinceglb.filekit.core.PlatformDirectory

actual fun localUriToDisplayPath(path: String): String {
    return android.net.Uri.parse(path).pathSegments?.lastOrNull()?.split(":")?.lastOrNull()
        .orEmpty()
}

actual val PlatformDirectory.pathString: String
    get() = uri.toString()
