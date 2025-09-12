package com.sorrowblue.comicviewer.framework.ui

import com.sorrowblue.comicviewer.framework.common.PlatformContext
import org.koin.core.annotation.Single

@Single
expect class NotificationManager(context: PlatformContext) {

    fun toast(text: String, length: Int)

    companion object {
        val LENGTH_SHORT: Int
    }
}
