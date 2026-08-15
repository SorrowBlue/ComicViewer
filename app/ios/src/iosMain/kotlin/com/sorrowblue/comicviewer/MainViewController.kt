/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.window.ComposeUIViewController
import com.sorrowblue.comicviewer.framework.common.Initializer

@Suppress("FunctionNaming")
fun MainViewController() = ComposeUIViewController {
    val iosApplication = IosApplication()
    context(iosApplication) {
        Application(finishApp = {})

        LaunchedEffect(Unit) {
            Initializer.initialize(iosApplication.appGraph.initializer.toList())
        }
    }
}
