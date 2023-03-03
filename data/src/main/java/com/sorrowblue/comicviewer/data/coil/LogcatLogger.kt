package com.sorrowblue.comicviewer.data.coil

import android.util.Log
import coil.util.Logger
import java.io.PrintWriter
import java.io.StringWriter
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

class LogcatLogger @JvmOverloads constructor(level: Int = Log.DEBUG) : Logger {

    override var level = level
        set(value) {
            assertValidLevel(value)
            field = value
        }

    init {
        assertValidLevel(level)
    }

    override fun log(tag: String, priority: Int, message: String?, throwable: Throwable?) {
        val lp = when (priority) {
            Log.ASSERT -> LogPriority.ASSERT
            Log.DEBUG -> LogPriority.DEBUG
            Log.ERROR -> LogPriority.ERROR
            Log.INFO -> LogPriority.INFO
            Log.WARN -> LogPriority.WARN
            else -> LogPriority.VERBOSE
        }
        if (message != null) {
            logcat(lp, tag) { message }
        }

        if (throwable != null) {
            val writer = StringWriter()
            throwable.printStackTrace(PrintWriter(writer))
            logcat(lp, tag) { throwable.asLog() }
        }
    }

    private fun assertValidLevel(value: Int) {
        require(value in Log.VERBOSE..Log.ASSERT) { "Invalid log level: $value" }
    }
}
