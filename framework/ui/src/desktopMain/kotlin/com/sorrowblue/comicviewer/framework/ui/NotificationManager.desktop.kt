package com.sorrowblue.comicviewer.framework.ui

import com.sorrowblue.comicviewer.framework.common.PlatformContext
import org.koin.core.annotation.Single

@Single
actual class NotificationManager actual constructor(context: PlatformContext) {
    actual fun toast(text: String, length: Int) {
        // TODO
    }

    actual companion object {
        actual val LENGTH_SHORT = 0
    }
}
