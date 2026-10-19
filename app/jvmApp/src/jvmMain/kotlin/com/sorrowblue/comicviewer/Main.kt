/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.platform.registerSkikoComposeImplementation
import androidx.compose.ui.window.application
import com.sorrowblue.comicviewer.app.MainWindow
import com.sorrowblue.comicviewer.app.initFileKit
import java.awt.Window
import java.io.File
import kotlin.system.exitProcess
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout

@OptIn(InternalComposeUiApi::class)
fun main(args: Array<String>) {
    val isSmokeTest = args.contains("--smoke-test")

    if (isSmokeTest) {
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            handleSmokeTestFailure("Uncaught exception in thread ${thread.name}", throwable)
        }
    }

    @Suppress("TooGenericExceptionCaught")
    try {
        registerSkikoComposeImplementation()
        initFileKit(
            appId = if (isSmokeTest) {
                "com.sorrowblue.comicviewer.smoketest"
            } else {
                "com.sorrowblue.comicviewer"
            },
        )
        val jvmApplication = JvmApplication()
        application {
            context(jvmApplication.appGraph) {
                MainWindow(exitApplication = ::exitApplication)
            }
            if (isSmokeTest) {
                LaunchedEffect(Unit) {
                    withTimeout(20.seconds) {
                        while (Window.getWindows().none { it.isShowing }) {
                            delay(100.milliseconds)
                        }
                    }
                    delay(2.seconds)
                    exitApplication()
                }
            }
        }
    } catch (t: Throwable) {
        if (isSmokeTest) {
            handleSmokeTestFailure("Exception during application startup", t)
        } else {
            throw t
        }
    }
}

private fun handleSmokeTestFailure(message: String, throwable: Throwable): Nothing {
    val report = "$message:\n${throwable.stackTraceToString()}"
    System.err.println(report)
    val logPath = System.getenv("COMICVIEWER_SMOKE_TEST_LOG") ?: "smoke-test-error.log"
    runCatching {
        File(logPath).writeText(report)
    }
    exitProcess(1)
}
