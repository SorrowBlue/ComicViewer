/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer

import androidx.compose.ui.window.application
import com.sorrowblue.comicviewer.app.MainWindow
import com.sorrowblue.comicviewer.app.initFileKit

fun main() {
    initFileKit(appId = "com.sorrowblue.comicviewer")
    val jvmApplication = JvmApplication()
    application {
        context(jvmApplication.appGraph) {
            MainWindow(exitApplication = ::exitApplication)
        }
    }
}

