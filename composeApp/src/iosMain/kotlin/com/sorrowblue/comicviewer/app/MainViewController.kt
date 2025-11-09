package com.sorrowblue.comicviewer.app

import androidx.compose.ui.window.ComposeUIViewController
import com.sorrowblue.comicviewer.Application
import com.sorrowblue.comicviewer.framework.common.IosContextImpl

@Suppress("FunctionNaming")
fun MainViewController() = ComposeUIViewController {
    val context = IosContextImpl()
    with(context) {
        Application()
    }
}
