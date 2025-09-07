package com.sorrowblue.comicviewer.framework.ui

import org.koin.core.annotation.Single

@Single
actual class NotificationManager {
    actual fun toast(text: String, length: Int) {
        // TODO
    }

    actual companion object {
        actual val LENGTH_SHORT: Int = 0
    }
}
