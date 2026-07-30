/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer

import androidx.compose.ui.window.ComposeUIViewController

@Suppress("FunctionNaming")
fun MainViewController() = ComposeUIViewController {
    val iosApplication = IosApplication()
    context(iosApplication, iosApplication.appGraph) {
        Application(finishApp = {})
    }
}
