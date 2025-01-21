package com.sorrowblue.comicviewer.feature.bookshelf.edit.component

import android.content.Context
import android.content.Intent
import androidx.core.uri.Uri
import org.koin.core.annotation.Singleton

@Singleton
internal actual class TakePersistableUriPermission(private val context: Context) {

    actual operator fun invoke(uri: Uri) {
        context.contentResolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        )
    }
}
