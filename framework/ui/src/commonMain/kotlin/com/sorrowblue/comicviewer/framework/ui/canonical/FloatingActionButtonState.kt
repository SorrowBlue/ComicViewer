package com.sorrowblue.comicviewer.framework.ui.canonical

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.sorrowblue.comicviewer.framework.ui.canonical.FloatingActionButtonStateImpl.Companion.saver

enum class FloatingActionButtonValue {
    Visible,
    Hidden,
}

val FloatingActionButtonValue.isVisible
    get() = this == FloatingActionButtonValue.Visible

@Stable
interface FloatingActionButtonState {
    val isAnimating: Boolean

    val targetValue: FloatingActionButtonValue

    val currentValue: FloatingActionButtonValue

    suspend fun snapTo(targetValue: FloatingActionButtonValue)

    val menuExpanded: Boolean

    fun toggleMenu(force: Boolean)
}

@Composable
fun rememberFloatingActionButtonState(
    initialValue: FloatingActionButtonValue = FloatingActionButtonValue.Visible,
): FloatingActionButtonState = rememberSaveable(saver = saver()) {
    FloatingActionButtonStateImpl(initialValue = initialValue)
}

private class FloatingActionButtonStateImpl(var initialValue: FloatingActionButtonValue) :
    FloatingActionButtonState {
    private val internalValue: Float = if (initialValue.isVisible) Visible else Hidden
    private val internalState = Animatable(internalValue, Float.VectorConverter)

    override val currentValue by derivedStateOf {
        if (internalState.value == Visible) {
            FloatingActionButtonValue.Visible
        } else {
            FloatingActionButtonValue.Hidden
        }
    }

    override val isAnimating: Boolean
        get() = internalState.isRunning

    override val targetValue: FloatingActionButtonValue
        get() =
            if (internalState.targetValue == Visible) {
                FloatingActionButtonValue.Visible
            } else {
                FloatingActionButtonValue.Hidden
            }

    override suspend fun snapTo(targetValue: FloatingActionButtonValue) {
        val target = if (targetValue.isVisible) Visible else Hidden
        internalState.snapTo(target)
    }

    override var menuExpanded: Boolean by mutableStateOf(false)

    override fun toggleMenu(force: Boolean) {
        menuExpanded = force
    }

    companion object {
        private const val Hidden = 0f
        private const val Visible = 1f

        /** The default [saver] implementation for [NavigationSuiteScaffoldState]. */
        fun saver() = Saver<FloatingActionButtonState, FloatingActionButtonValue>(
            save = { it.targetValue },
            restore = { FloatingActionButtonStateImpl(it) },
        )
    }
}
