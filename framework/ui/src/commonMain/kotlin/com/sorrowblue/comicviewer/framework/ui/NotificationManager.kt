package com.sorrowblue.comicviewer.framework.ui

expect class NotificationManager {

    fun toast(text: String, length: Int)


    companion object {
        val LENGTH_SHORT: Int
    }
}
