package com.sorrowblue.comicviewer.app

import androidx.compose.ui.window.ComposeUIViewController
import com.sorrowblue.comicviewer.Application
import com.sorrowblue.comicviewer.framework.common.IosContext
import com.sorrowblue.comicviewer.framework.common.IosContextImpl
import dev.zacsweers.metro.createGraphFactory

@Suppress("FunctionNaming")
fun MainViewController() = ComposeUIViewController {
    val context = IosContextImpl()
    val appGraph =
        createGraphFactory<AppGraph.Factory>().createAppGraph(context, LicenseeHelperImpl())
    IosContext.platformGraph = appGraph
    with(context) {
        with(appGraph) {
            Application(finishApp = {})
        }
    }
}
