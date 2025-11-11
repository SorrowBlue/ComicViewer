package com.sorrowblue.comicviewer.app

import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.ComposeUiFlags
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.splashscreen.SplashScreenViewProvider
import com.sorrowblue.comicviewer.ComicViewerUI
import com.sorrowblue.comicviewer.feature.book.navigation.ReceiveBookKey
import com.sorrowblue.comicviewer.rememberComicViewerUIContext
import com.sorrowblue.comicviewer.rememberComicViewerUIState
import logcat.logcat

/** Main activity */
internal class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        logcat { "onCreate" }
        installSplashScreen().apply {
            enableEdgeToEdge(
                navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
            )
            super.onCreate(savedInstanceState)
            setOnExitAnimationListener(SplashScreenViewProvider::startShrinkingAnimation)
            setKeepOnScreenCondition(viewModel.shouldKeepSplash::value)
        }

        val receivedBookData = if (intent.action == Intent.ACTION_VIEW &&
            (
                intent.categories == null || intent.hasCategory(
                    Intent.CATEGORY_BROWSABLE,
                ) || intent.hasCategory(
                    Intent.CATEGORY_DEFAULT,
                )
                ) &&
            intent.scheme in listOf("file", "content") &&
            intent.type in listOf("application/pdf", "application/zip")
        ) {
            intent.dataString
        } else {
            null
        }

        @OptIn(ExperimentalComposeUiApi::class)
        ComposeUiFlags.isSemanticAutofillEnabled = true
        setContent {
            with(rememberComicViewerUIContext()) {
                val state = rememberComicViewerUIState(
                    allowNavigationRestored = receivedBookData.isNullOrEmpty()
                )
                ComicViewerUI(finishApp = ::finish, state = state)
                LaunchedEffect(receivedBookData.isNullOrEmpty()) {
                    if (!receivedBookData.isNullOrEmpty()) {
                        state.navigation3State.addToBackStack(ReceiveBookKey(receivedBookData))
                        state.onNavigationHistoryRestore()
                    }
                }
            }
        }
    }
}

/** Start shrinking animation */
private fun SplashScreenViewProvider.startShrinkingAnimation() {
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
