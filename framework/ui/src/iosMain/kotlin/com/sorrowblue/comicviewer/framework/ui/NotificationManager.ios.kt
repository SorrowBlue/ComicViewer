package com.sorrowblue.comicviewer.framework.ui

import com.sorrowblue.comicviewer.framework.common.PlatformContext
import jakarta.inject.Singleton

@Singleton
actual class NotificationManager actual constructor(context: PlatformContext) {
    actual fun toast(text: String, length: Int) {
        // TODO
    }

    actual companion object {
        actual val LENGTH_SHORT: Int = 0
    }
}
