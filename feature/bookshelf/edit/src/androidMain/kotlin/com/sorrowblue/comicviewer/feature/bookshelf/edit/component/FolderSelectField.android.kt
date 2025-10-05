package com.sorrowblue.comicviewer.feature.bookshelf.edit.component

import androidx.core.net.toUri

actual fun localUriToDisplayPath(path: String): String {
    return path.toUri().pathSegments?.lastOrNull()?.split(":")?.lastOrNull()
        .orEmpty()
}
