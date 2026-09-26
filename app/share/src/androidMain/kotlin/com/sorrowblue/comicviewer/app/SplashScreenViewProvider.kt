/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.app

import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreenViewProvider

/** Start shrinking animation */
internal fun SplashScreenViewProvider.startShrinkingAnimation() {
    runCatching {
        // 残りアニメーション時間の計算
        val remainingDuration =
            iconAnimationDurationMillis - System.currentTimeMillis() + iconAnimationStartMillis
        val animDuration = if (remainingDuration < 0) 300L else remainingDuration

        ObjectAnimator
            .ofFloat(view, View.TRANSLATION_Y, 0f, view.height.toFloat())
            .apply {
                interpolator = AccelerateInterpolator()
                duration = animDuration
                doOnEnd { remove() }
            }
            .start()
    }.onFailure { remove() }
}
