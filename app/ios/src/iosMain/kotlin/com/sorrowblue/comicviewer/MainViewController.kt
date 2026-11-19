/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer

import androidx.compose.ui.window.ComposeUIViewController
import com.sorrowblue.comicviewer.app.MetroContent

@Suppress("FunctionNaming")
fun MainViewController() = ComposeUIViewController {
    val iosApplication = IosApplication()
    context(iosApplication) {
        MetroContent {
            Application(finishApp = {})
        }
    }
}
