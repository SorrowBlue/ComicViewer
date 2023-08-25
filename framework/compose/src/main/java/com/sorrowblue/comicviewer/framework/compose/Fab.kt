package com.sorrowblue.comicviewer.framework.compose

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.delay

@Composable
fun LocalLifecycleState(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    onCreate: () -> Unit = {},
    onStart: () -> Unit = {},
    onResume: () -> Unit = {},
    onPause: () -> Unit = {},
    onStop: () -> Unit = {},
    onDestroy: () -> Unit = {}
) {
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> onCreate()
                Lifecycle.Event.ON_START -> onStart()
                Lifecycle.Event.ON_RESUME -> onResume()
                Lifecycle.Event.ON_PAUSE -> onPause()
                Lifecycle.Event.ON_STOP -> onStop()
                Lifecycle.Event.ON_DESTROY -> onDestroy()
                Lifecycle.Event.ON_ANY -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}


class FabVisibleState {

    val state = MutableTransitionState(false)

    var icon: ImageVector? = null
    var onClick: () -> Unit = {}

    suspend fun show(icon: ImageVector, onClick: () -> Unit) {
        if (state.currentState) {
            // 既に表示されている場合は、一回非表示にする。
            state.targetState = false
            // 遷移完了するまで待機
            while (true) {
                delay(100)
                if (state.isIdle) break
            }

            this.icon = icon
            this.onClick = onClick
            state.targetState = true
        } else {
            this.icon = icon
            this.onClick = onClick
            state.targetState = true
        }
    }

    fun hide() {
        state.targetState = false
    }
}
