package com.sorrowblue.comicviewer.feature.bookshelf.edit.component

import android.content.Context
import android.content.Intent
import io.github.vinceglb.filekit.core.PlatformDirectory
import org.koin.core.annotation.Singleton

@Singleton
internal actual class TakePersistableUriPermission(private val context: Context) {

    actual operator fun invoke(platformDirectory: PlatformDirectory) {
        context.contentResolver.takePersistableUriPermission(
            platformDirectory.uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        )
    }
}
