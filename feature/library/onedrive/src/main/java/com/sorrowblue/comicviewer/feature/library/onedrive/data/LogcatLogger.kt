package com.sorrowblue.comicviewer.feature.library.onedrive.data

import com.microsoft.graph.logger.ILogger
import com.microsoft.graph.logger.LoggerLevel
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

internal object LogcatLogger : ILogger {
    private var level = LoggerLevel.DEBUG

    override fun setLoggingLevel(level: LoggerLevel) {
        this.level = level
    }

    override fun getLoggingLevel(): LoggerLevel {
        return level
    }

    override fun logDebug(message: String) {
        logcat(priority = LogPriority.DEBUG) { message }
    }

    override fun logError(message: String, throwable: Throwable?) {
        throwable?.printStackTrace()
        logcat(priority = LogPriority.ERROR) { message + ":${throwable?.asLog()}" }
    }
}
