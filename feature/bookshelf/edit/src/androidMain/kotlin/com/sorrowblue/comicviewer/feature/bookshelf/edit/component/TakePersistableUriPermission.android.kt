package com.sorrowblue.comicviewer.feature.bookshelf.edit.component

import android.content.Intent
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import io.github.vinceglb.filekit.core.PlatformDirectory
import org.koin.core.annotation.Single

@Single
internal actual class TakePersistableUriPermission actual constructor(private val context: PlatformContext) {

    actual operator fun invoke(platformDirectory: PlatformDirectory) {
        context.contentResolver.takePersistableUriPermission(
            platformDirectory.uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        )
    }
}
