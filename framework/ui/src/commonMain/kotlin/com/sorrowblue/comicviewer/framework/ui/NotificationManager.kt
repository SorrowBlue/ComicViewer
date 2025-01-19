package com.sorrowblue.comicviewer.framework.ui

import androidx.compose.runtime.Composable

expect class NotificationManager {

    fun toast(text: String, length: Int)


    companion object {
        val LENGTH_SHORT: Int
    }
}
