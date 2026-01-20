package com.sorrowblue.comicviewer.app

import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreenViewProvider

/** Start shrinking animation */
internal fun SplashScreenViewProvider.startShrinkingAnimation() {
    runCatching {
        ObjectAnimator
            .ofFloat(view, View.SCALE_X, 1f, 0f)
            .apply {
                interpolator = AnticipateInterpolator()
                doOnEnd { remove() }
                duration =
                    if (iconAnimationDurationMillis - System.currentTimeMillis() +
                        iconAnimationStartMillis <
                        0
                    ) {
                        300
                    } else {
                        iconAnimationDurationMillis - System.currentTimeMillis() +
                            iconAnimationStartMillis
                    }
            }.start()
        ObjectAnimator
            .ofFloat(view, View.SCALE_Y, 1f, 0f)
            .apply {
                interpolator = AnticipateInterpolator()
                doOnEnd { remove() }
                duration =
                    if (iconAnimationDurationMillis - System.currentTimeMillis() +
                        iconAnimationStartMillis <
                        0
                    ) {
                        300
                    } else {
                        iconAnimationDurationMillis - System.currentTimeMillis() +
                            iconAnimationStartMillis
                    }
            }.start()
    }.onFailure { remove() }
}
