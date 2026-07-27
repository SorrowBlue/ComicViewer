/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer

import androidx.compose.ui.window.ComposeUIViewController
import com.sorrowblue.comicviewer.app.AppGraph
import com.sorrowblue.comicviewer.framework.common.IosContext
import com.sorrowblue.comicviewer.framework.common.getPlatformGraph
import dev.zacsweers.metro.createGraphFactory

@Suppress("FunctionNaming")
fun MainViewController() = ComposeUIViewController {
    val context = IosContext.Companion()
    val appGraph =
        createGraphFactory<AppGraph.Factory>().createAppGraph(context, LicenseeHelperImpl())
    getPlatformGraph = { appGraph }
    with(context) {
        with(appGraph) {
            Application(finishApp = {})
        }
    }
}
