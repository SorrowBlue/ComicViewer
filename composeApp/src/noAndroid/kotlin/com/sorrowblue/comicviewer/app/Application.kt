package com.sorrowblue.comicviewer.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import com.sorrowblue.comicviewer.AppContext
import com.sorrowblue.comicviewer.framework.common.Initializer
import com.sorrowblue.comicviewer.framework.common.LocalPlatformContext
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme

@Composable
context(context: AppContext)
fun Application(finishApp: () -> Unit) {
    LaunchedEffect(Unit) {
        Initializer.initialize(context.initializer.toList())
    }
    CompositionLocalProvider(LocalPlatformContext provides context.platformContext) {
        ComicTheme {
//            RootScreenWrapper(finishApp = finishApp) {
//                ComicViewerApp()
//            }
        }
    }
}
