package com.sorrowblue.comicviewer.feature.bookshelf.edit.component

import androidx.core.uri.Uri

actual fun localUriToDisplayPath(uri: Uri): String {
    return uri.toString()
}
