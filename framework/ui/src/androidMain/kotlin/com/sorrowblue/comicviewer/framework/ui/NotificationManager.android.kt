package com.sorrowblue.comicviewer.framework.ui

import android.content.Context
import android.widget.Toast
import org.koin.core.annotation.Singleton

@Singleton
actual class NotificationManager(private val context: Context) {
    actual fun toast(text: String, length: Int) {
        Toast.makeText(context, text, length).show()
    }

    actual companion object {
        actual const val LENGTH_SHORT: Int = Toast.LENGTH_SHORT
    }
}
