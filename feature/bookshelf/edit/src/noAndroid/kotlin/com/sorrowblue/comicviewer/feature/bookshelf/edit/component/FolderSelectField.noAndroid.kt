package com.sorrowblue.comicviewer.feature.bookshelf.edit.component

import androidx.core.uri.Uri
import org.koin.core.annotation.Singleton

@Singleton
internal actual class TakePersistableUriPermission {
    actual operator fun invoke(uri: Uri) {
    }
}
