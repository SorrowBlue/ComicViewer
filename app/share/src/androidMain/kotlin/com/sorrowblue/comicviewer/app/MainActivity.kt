package com.sorrowblue.comicviewer.app

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.splashscreen.SplashScreenViewProvider
import com.sorrowblue.comicviewer.ComicViewerUI
import com.sorrowblue.comicviewer.feature.book.navigation.ReceiveBookNavKey
import com.sorrowblue.comicviewer.rememberComicViewerUIContext
import com.sorrowblue.comicviewer.rememberComicViewerUIState
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.android.ActivityKey

/**
 * Main activity
 */
@ContributesIntoMap(AppScope::class, binding<Activity>())
@ActivityKey(MainActivity::class)
class MainActivity(private val appGraph: AppGraph) : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
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
        setContent {
            val state = with(rememberComicViewerUIContext()) {
                rememberComicViewerUIState(
                    allowNavigationRestored = receivedBookData.isNullOrEmpty(),
                )
            }
            with(appGraph) {
                ComicViewerUI(finishApp = ::finish, state = state)
            }
            LaunchedEffect(receivedBookData.isNullOrEmpty()) {
                if (!receivedBookData.isNullOrEmpty()) {
                    state.navigator.navigate(ReceiveBookNavKey(receivedBookData))
                    state.onNavigationHistoryRestore()
                }
            }
        }
    }
}
