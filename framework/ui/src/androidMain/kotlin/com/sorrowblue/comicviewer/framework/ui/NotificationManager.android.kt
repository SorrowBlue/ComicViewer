package com.sorrowblue.comicviewer.framework.ui

import android.widget.Toast
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import dev.zacsweers.metro.Inject

@Inject
actual class NotificationManager actual constructor(private val context: PlatformContext) {
    actual fun toast(text: String, length: Int) {
        Toast.makeText(context, text, length).show()
    }

    actual companion object {
        actual const val LENGTH_SHORT: Int = Toast.LENGTH_SHORT
    }
}
