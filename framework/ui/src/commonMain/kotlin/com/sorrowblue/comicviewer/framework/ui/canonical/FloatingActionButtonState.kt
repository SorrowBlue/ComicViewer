package com.sorrowblue.comicviewer.framework.ui.canonical

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.VectorConverter
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import com.sorrowblue.comicviewer.framework.designsystem.theme.ExpressiveMotion

enum class FloatingActionButtonValue {
    Visible,
    Hidden
}

val FloatingActionButtonValue.isVisible
    get() = this == FloatingActionButtonValue.Visible

@Stable
interface FloatingActionButtonState {

    val isAnimating: Boolean

    val targetValue: FloatingActionButtonValue

    val currentValue: FloatingActionButtonValue

    suspend fun hide()

    suspend fun show()

    suspend fun toggle()

    suspend fun snapTo(targetValue: FloatingActionButtonValue)
}

@Composable
fun rememberFloatingActionButtonState(
    initialValue: FloatingActionButtonValue = FloatingActionButtonValue.Visible,
): FloatingActionButtonState {
    return rememberSaveable(saver = FloatingActionButtonStateImpl.Saver()) {
        FloatingActionButtonStateImpl(initialValue = initialValue)
    }
}

private class FloatingActionButtonStateImpl(var initialValue: FloatingActionButtonValue) :
    FloatingActionButtonState {
    private val internalValue: Float = if (initialValue.isVisible) Visible else Hidden
    private val internalState = Animatable(internalValue, Float.VectorConverter)
    private val _currentVal = derivedStateOf {
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

    override val currentValue: FloatingActionButtonValue
        get() = _currentVal.value

    override suspend fun hide() {
        internalState.animateTo(targetValue = Hidden, animationSpec = AnimationSpec)
    }

    override suspend fun show() {
        internalState.animateTo(targetValue = Visible, animationSpec = AnimationSpec)
    }

    override suspend fun toggle() {
        internalState.animateTo(
            targetValue = if (targetValue.isVisible) Hidden else Visible,
            animationSpec = AnimationSpec
        )
    }

    override suspend fun snapTo(targetValue: FloatingActionButtonValue) {
        val target = if (targetValue.isVisible) Visible else Hidden
        internalState.snapTo(target)
    }

    companion object {
        private const val Hidden = 0f
        private const val Visible = 1f

        /** The default [Saver] implementation for [NavigationSuiteScaffoldState]. */
        fun Saver() =
            Saver<FloatingActionButtonState, FloatingActionButtonValue>(
                save = { it.targetValue },
                restore = { FloatingActionButtonStateImpl(it) }
            )
    }
}

private val AnimationSpec: SpringSpec<Float> = ExpressiveMotion.Spatial.default()
