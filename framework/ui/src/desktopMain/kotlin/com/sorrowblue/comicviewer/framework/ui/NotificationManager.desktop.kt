package com.sorrowblue.comicviewer.framework.ui

import org.koin.core.annotation.Singleton

@Singleton
actual class NotificationManager {
    actual fun toast(text: String, length: Int) {
        // TODO
    }

    actual companion object {
        actual val LENGTH_SHORT = 0
    }
}
