package com.sorrowblue.comicviewer.framework.ui

import com.sorrowblue.comicviewer.framework.common.PlatformContext
import dev.zacsweers.metro.Inject

@Inject
actual class NotificationManager actual constructor(context: PlatformContext) {
    actual fun toast(text: String, length: Int) {
        // TODO
    }

    actual companion object {
        actual val LENGTH_SHORT: Int = 0
    }
}
