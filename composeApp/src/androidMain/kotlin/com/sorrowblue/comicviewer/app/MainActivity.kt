package com.sorrowblue.comicviewer.app

import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.ComposeUiFlags
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.splashscreen.SplashScreenViewProvider
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import logcat.logcat
import org.koin.androidx.compose.KoinAndroidContext

/**
 * Main activity
 */
internal class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        logcat { "onCreate" }
        installSplashScreen().apply {
            enableEdgeToEdge(
                navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT)
            )
            super.onCreate(savedInstanceState)
            setOnExitAnimationListener(SplashScreenViewProvider::startShrinkingAnimation)
            setKeepOnScreenCondition(viewModel.shouldKeepSplash::value)
            com.sorrowblue.comicviewer.framework.ui.R.string.app_label_history
        }

        @OptIn(ExperimentalComposeUiApi::class)
        ComposeUiFlags.isSemanticAutofillEnabled = true
        setContent {
            ComicTheme {
                KoinAndroidContext {
                    RootScreenWrapper(finishApp = ::finish) {
                        ComicViewerApp()
                    }
                }
            }
        }
    }
}

/**
 * Start shrinking animation
 */
private fun SplashScreenViewProvider.startShrinkingAnimation() {
    kotlin.runCatching {
        ObjectAnimator.ofFloat(view, View.SCALE_X, 1f, 0f).apply {
            interpolator = AnticipateInterpolator()
            doOnEnd { remove() }
            duration =
                if (iconAnimationDurationMillis - System.currentTimeMillis() + iconAnimationStartMillis < 0) 300 else iconAnimationDurationMillis - System.currentTimeMillis() + iconAnimationStartMillis
        }.start()
        ObjectAnimator.ofFloat(view, View.SCALE_Y, 1f, 0f).apply {
            interpolator = AnticipateInterpolator()
            doOnEnd { remove() }
            duration =
                if (iconAnimationDurationMillis - System.currentTimeMillis() + iconAnimationStartMillis < 0) 300 else iconAnimationDurationMillis - System.currentTimeMillis() + iconAnimationStartMillis
        }.start()
    }.onFailure { remove() }
}
