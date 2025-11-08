package com.sorrowblue.comicviewer.framework.ui

import com.sorrowblue.comicviewer.framework.common.PlatformContext
import dev.zacsweers.metro.Inject

@Inject
expect class NotificationManager(context: PlatformContext) {
    fun toast(text: String, length: Int)

    companion object {
        val LengthShort: Int
    }
}
