package com.sorrowblue.comicviewer.framework.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberSystemUiController(): SystemUiController {
    return remember { UnSupportSystemUiController }
}

private object UnSupportSystemUiController : SystemUiController {
    override var systemBarsBehavior = -1
    override var isStatusBarVisible = true
    override var isNavigationBarVisible = true
}
