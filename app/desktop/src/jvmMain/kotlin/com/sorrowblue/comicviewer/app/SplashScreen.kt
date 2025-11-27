package com.sorrowblue.comicviewer.app

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.framework.ui.animation.materialSharedAxisZIn
import com.sorrowblue.comicviewer.framework.ui.animation.materialSharedAxisZOut
import comicviewer.app.desktop.generated.resources.Res
import kotlinx.coroutines.delay
import org.jetbrains.compose.animatedimage.AnimatedImage
import org.jetbrains.compose.animatedimage.animate
import org.jetbrains.compose.animatedimage.loadAnimatedImage

@Composable
fun SplashScreen(keepOnScreenCondition: () -> Boolean) {
    var shouldKeepSplash by remember { mutableStateOf(true) }
    val currentKeepOnScreenCondition by rememberUpdatedState(keepOnScreenCondition)
    LaunchedEffect(Unit) {
        do {
            delay(2000)
            shouldKeepSplash = currentKeepOnScreenCondition()
        } while (shouldKeepSplash)
    }
    AnimatedVisibility(
        visible = shouldKeepSplash,
        enter = materialSharedAxisZIn(),
        exit = materialSharedAxisZOut(),
    ) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color(235, 236, 242)),
            contentAlignment = Alignment.Center,
        ) {
            val image by produceState<AnimatedImage?>(null) {
                value = loadAnimatedImage(Res.getUri("files/ic_launcher.gif"))
            }
            image?.let {
                Image(
                    bitmap = it.animate(),
                    contentDescription = null,
                    modifier = Modifier.size(120.dp),
                )
            }
        }
    }
}
