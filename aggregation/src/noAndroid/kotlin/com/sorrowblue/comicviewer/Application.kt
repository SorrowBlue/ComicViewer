package com.sorrowblue.comicviewer

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.sorrowblue.comicviewer.app.PreAppScreen
import com.sorrowblue.comicviewer.app.PreAppScreenContext
import com.sorrowblue.comicviewer.framework.common.Initializer
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import com.sorrowblue.comicviewer.framework.common.require
import com.sorrowblue.comicviewer.framework.ui.animation.materialSharedAxisZIn
import com.sorrowblue.comicviewer.framework.ui.animation.materialSharedAxisZOut
import io.github.takahirom.rin.rememberRetained
import kotlinx.coroutines.delay

@Composable
context(context: PlatformContext)
fun Application() {
    var isKeepSplash by remember { mutableStateOf(true) }
    val enter = materialSharedAxisZIn()
    val exit = materialSharedAxisZOut()
    AnimatedContent(
        isKeepSplash,
        transitionSpec = { enter togetherWith exit }
    ) {
        if (isKeepSplash) {
            SplashScreen()
        } else {
            val preAppScreenContext = rememberRetained {
                context.require<PreAppScreenContext.Factory>().createPreAppScreenContext()
            }
            with(preAppScreenContext) {
                PreAppScreen(finishApp = {}) {
                    ComicViewerUI(null)
                }
            }
        }
    }
    LaunchedEffect(Unit) {
        if (isKeepSplash) {
            Initializer.initialize(context.require<AppContext>().initializer.toList())
            delay(2000)
            isKeepSplash = false
        }
    }
}
