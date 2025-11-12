package com.sorrowblue.comicviewer.feature.bookshelf.edit.component

import platform.Foundation.NSURL

actual fun localUriToDisplayPath(path: String): String =
    NSURL(string = path).lastPathComponent.orEmpty()
