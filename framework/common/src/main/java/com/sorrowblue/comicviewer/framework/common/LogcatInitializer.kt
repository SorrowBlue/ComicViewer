package com.sorrowblue.comicviewer.framework.common

import android.app.Application
import android.content.Context
import androidx.startup.Initializer
import dagger.hilt.android.EntryPointAccessors
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

interface BaseInitializerEntryPoint<T> {

    interface CompanionObject {

        fun <T> resolve(context: Context): BaseInitializerEntryPoint<T> {
            val appContext = checkNotNull(context.applicationContext)
            @Suppress("UNCHECKED_CAST")
            return EntryPointAccessors.fromApplication(
                appContext,
                BaseInitializerEntryPoint::class.java
            ) as BaseInitializerEntryPoint<T>
        }
    }

    fun inject(initializer: T)
}
