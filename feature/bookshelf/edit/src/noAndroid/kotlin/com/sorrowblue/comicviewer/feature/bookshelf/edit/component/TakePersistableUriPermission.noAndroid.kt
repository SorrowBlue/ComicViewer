package com.sorrowblue.comicviewer.feature.bookshelf.edit.component

import com.sorrowblue.comicviewer.framework.common.PlatformContext
import io.github.vinceglb.filekit.core.PlatformDirectory
import org.koin.core.annotation.Single

@Single
internal actual class TakePersistableUriPermission actual constructor(context: PlatformContext) {
    actual operator fun invoke(platformDirectory: PlatformDirectory) {
        // TODO
    }
}
