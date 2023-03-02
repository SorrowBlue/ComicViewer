package com.sorrowblue.comicviewer.framework

import android.app.Application
import android.content.Context
import androidx.startup.Initializer
import logcat.AndroidLogcatLogger
import logcat.LogPriority
import logcat.LogcatLogger
import logcat.logcat

class LogcatInitializer : Initializer<LogcatLogger.Companion> {
    override fun create(context: Context): LogcatLogger.Companion {
        if (BuildConfig.BUILD_TYPE != "release") {
            AndroidLogcatLogger.installOnDebuggableApp(context as Application, LogPriority.VERBOSE)
            logcat(LogPriority.INFO) { "Initialize logcat." }
        }
        return LogcatLogger.Companion
    }

    override fun dependencies() = emptyList<Class<Initializer<*>>>()
}
