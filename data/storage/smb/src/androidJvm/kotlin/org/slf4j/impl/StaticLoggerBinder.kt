/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package org.slf4j.impl

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import org.slf4j.ILoggerFactory
import org.slf4j.Logger
import org.slf4j.spi.LoggerFactoryBinder

internal class StaticLoggerBinder : LoggerFactoryBinder {
    private val loggerFactory: ILoggerFactory = object : ILoggerFactory {
        private val map: ConcurrentMap<String?, Logger?> = ConcurrentHashMap<String, Logger>()
        override fun getLogger(name: String?): Logger? =
            map.computeIfAbsent(name, ::LogcatLoggerAdapter)
    }

    override fun getLoggerFactory(): ILoggerFactory = loggerFactory

    override fun getLoggerFactoryClassStr(): String = loggerFactory.javaClass.getName()

    companion object {
        @JvmStatic
        val singleton: StaticLoggerBinder = StaticLoggerBinder()

        @JvmField
        var REQUESTED_API_VERSION: String = "1.7.36"
    }
}
