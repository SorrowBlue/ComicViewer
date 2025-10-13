package com.sorrowblue.comicviewer.framework.ui

import com.sorrowblue.comicviewer.framework.common.PlatformContext
import jakarta.inject.Singleton

@Singleton
expect class NotificationManager(context: PlatformContext) {

    fun toast(text: String, length: Int)

    companion object {
        val LENGTH_SHORT: Int
    }
}
