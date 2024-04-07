package com.sorrowblue.comicviewer.framework.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun LifecycleResumeEffect(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    action: () -> Unit,
) {
    LifecycleEffect(Lifecycle.Event.ON_RESUME, lifecycleOwner, action)
}

@Composable
fun LifecycleEffect(
    targetEvent: Lifecycle.Event,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    action: () -> Unit,
) {
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == targetEvent) {
                action()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}
