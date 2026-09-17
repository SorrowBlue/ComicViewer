/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package org.slf4j.impl

import logcat.LogPriority
import logcat.asLog
import logcat.logcat
import org.slf4j.helpers.FormattingTuple
import org.slf4j.helpers.MarkerIgnoringBase
import org.slf4j.helpers.MessageFormatter

internal class LogcatLoggerAdapter(private val tag: String?) : MarkerIgnoringBase() {

    init {
        this.name = tag
    }

    override fun isInfoEnabled(): Boolean = true
    override fun info(msg: String?) = dispatch("INFO", msg, null)
    override fun info(format: String?, arg: Any?) = formatAndDispatch("INFO", format, arg)
    override fun info(format: String?, arg1: Any?, arg2: Any?) =
        formatAndDispatch("INFO", format, arg1, arg2)

    override fun info(format: String?, vararg arguments: Any?) =
        formatAndDispatch("INFO", format, *arguments)

    override fun info(msg: String?, t: Throwable?) = dispatch("INFO", msg, t)

    override fun isWarnEnabled(): Boolean = true
    override fun warn(msg: String?) = dispatch("WARN", msg, null)
    override fun warn(format: String?, arg: Any?) = formatAndDispatch("WARN", format, arg)
    override fun warn(format: String?, arg1: Any?, arg2: Any?) =
        formatAndDispatch("WARN", format, arg1, arg2)

    override fun warn(format: String?, vararg arguments: Any?) =
        formatAndDispatch("WARN", format, *arguments)

    override fun warn(msg: String?, t: Throwable?) = dispatch("WARN", msg, t)

    override fun isErrorEnabled(): Boolean = true
    override fun error(msg: String?) = dispatch("ERROR", msg, null)
    override fun error(format: String?, arg: Any?) = formatAndDispatch("ERROR", format, arg)
    override fun error(format: String?, arg1: Any?, arg2: Any?) =
        formatAndDispatch("ERROR", format, arg1, arg2)

    override fun error(format: String?, vararg arguments: Any?) =
        formatAndDispatch("ERROR", format, *arguments)

    override fun error(msg: String?, t: Throwable?) = dispatch("ERROR", msg, t)

    override fun isDebugEnabled(): Boolean = true
    override fun debug(msg: String?) = dispatch("DEBUG", msg, null)
    override fun debug(format: String?, arg: Any?) = formatAndDispatch("DEBUG", format, arg)
    override fun debug(format: String?, arg1: Any?, arg2: Any?) =
        formatAndDispatch("DEBUG", format, arg1, arg2)

    override fun debug(format: String?, vararg arguments: Any?) =
        formatAndDispatch("DEBUG", format, *arguments)

    override fun debug(msg: String?, t: Throwable?) = dispatch("DEBUG", msg, t)

    override fun isTraceEnabled(): Boolean = true
    override fun trace(msg: String?) = dispatch("VERBOSE", msg, null)
    override fun trace(format: String?, arg: Any?) = formatAndDispatch("VERBOSE", format, arg)
    override fun trace(format: String?, arg1: Any?, arg2: Any?) =
        formatAndDispatch("VERBOSE", format, arg1, arg2)

    override fun trace(format: String?, vararg arguments: Any?) =
        formatAndDispatch("VERBOSE", format, *arguments)

    override fun trace(msg: String?, t: Throwable?) = dispatch("VERBOSE", msg, t)

    private fun formatAndDispatch(level: String, format: String?, vararg args: Any?) {
        val ft: FormattingTuple = MessageFormatter.arrayFormat(format, args)
        dispatch(level, ft.message, ft.throwable)
    }

    private fun dispatch(level: String, message: String?, t: Throwable?) {
        logcat(
            tag = tag,
            priority = when (level) {
                "INFO" -> LogPriority.INFO
                "WARN" -> LogPriority.WARN
                "ERROR" -> LogPriority.ERROR
                "VERBOSE" -> return
                "ASSERT" -> return
                else -> return
            },
        ) { "${message.orEmpty()} ${t?.asLog()}" }
    }
}
