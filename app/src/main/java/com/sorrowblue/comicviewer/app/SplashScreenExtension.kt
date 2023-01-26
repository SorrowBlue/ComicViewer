package com.sorrowblue.comicviewer.app

import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreenViewProvider

internal fun SplashScreenViewProvider.startSlideUpAnime() {
    kotlin.runCatching {
        val slideUp = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, 0f, -iconView.height * 2f)
        slideUp.interpolator = AnticipateInterpolator()
        slideUp.doOnEnd { remove() }
        slideUp.duration =
            if (iconAnimationDurationMillis - System.currentTimeMillis() + iconAnimationStartMillis < 0) 300 else iconAnimationDurationMillis - System.currentTimeMillis() + iconAnimationStartMillis
        slideUp.start()
    }.onFailure { remove() }
}
