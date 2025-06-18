package com.sorrowblue.comicviewer.framework.ui.canonical

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.VectorConverter
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import com.sorrowblue.comicviewer.framework.designsystem.theme.ExpressiveMotion

enum class AppBarValue {
    Visible,
    Hidden,
}

val AppBarValue.isVisible
    get() = this == AppBarValue.Visible

@Stable
interface AppBarState {

    val isAnimating: Boolean

    val targetValue: AppBarValue

    val currentValue: AppBarValue

    var scrollBehavior: TopAppBarScrollBehavior?

    suspend fun hide()

    suspend fun show()

    suspend fun toggle()

    suspend fun snapTo(targetValue: AppBarValue)
}

@Composable
fun rememberAppBarState(
    initialValue: AppBarValue = AppBarValue.Visible,
): AppBarState {
    return rememberSaveable(saver = AppBarStateImpl.saver()) {
        AppBarStateImpl(initialValue = initialValue)
    }
}

private class AppBarStateImpl(var initialValue: AppBarValue) : AppBarState {
    private val internalValue: Float = if (initialValue.isVisible) Visible else Hidden
    private val internalState = Animatable(internalValue, Float.VectorConverter)
    private val _currentVal = derivedStateOf {
        if (internalState.value == Visible) {
            AppBarValue.Visible
        } else {
            AppBarValue.Hidden
        }
    }

    override val isAnimating: Boolean
        get() = internalState.isRunning

    override val targetValue: AppBarValue
        get() =
            if (internalState.targetValue == Visible) {
                AppBarValue.Visible
            } else {
                AppBarValue.Hidden
            }

    override val currentValue: AppBarValue
        get() = _currentVal.value

    override var scrollBehavior: TopAppBarScrollBehavior? = null

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

    override suspend fun snapTo(targetValue: AppBarValue) {
        val target = if (targetValue.isVisible) Visible else Hidden
        internalState.snapTo(target)
    }

    companion object {
        private const val Hidden = 0f
        private const val Visible = 1f

        /** The default [saver] implementation for [NavigationSuiteScaffoldState]. */
        fun saver() =
            Saver<AppBarState, AppBarValue>(
                save = { it.targetValue },
                restore = { AppBarStateImpl(it) }
            )
    }
}

private val AnimationSpec: SpringSpec<Float> = ExpressiveMotion.Spatial.default()
