package com.sorrowblue.comicviewer.framework.ui

actual class NotificationManager {
    actual fun toast(text: String, length: Int) {
    }

    actual companion object {
        actual val LENGTH_SHORT: Int
            get() = TODO("Not yet implemented")
    }
}
