package com.sorrowblue.comicviewer.feature.bookshelf.edit.component

import io.github.vinceglb.filekit.core.PlatformDirectory
import org.koin.core.annotation.Singleton

@Singleton
internal actual class TakePersistableUriPermission {
    actual operator fun invoke(platformDirectory: PlatformDirectory) {
    }
}
