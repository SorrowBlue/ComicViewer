package com.sorrowblue.comicviewer.framework.common

import android.app.Application
import android.content.Context
import androidx.startup.Initializer
import logcat.AndroidLogcatLogger
import logcat.LogPriority
import logcat.LogcatLogger
import logcat.logcat

class LogcatInitializer : Initializer<LogcatLogger.Companion> {
    override fun create(context: Context): LogcatLogger.Companion {
        AndroidLogcatLogger.installOnDebuggableApp(context as Application, LogPriority.VERBOSE)
        logcat(LogPriority.INFO) { "Initialized logcat." }
        return LogcatLogger.Companion
    }

    override fun dependencies() = emptyList<Class<Initializer<*>>>()
}
