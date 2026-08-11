/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.splashscreen.SplashScreenViewProvider
import com.sorrowblue.comicviewer.feature.book.navigation.ReceiveBookNavKey
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.android.ActivityKey
import dev.zacsweers.metrox.viewmodel.LocalMetroViewModelFactory

/**
 * Main activity
 */
@ContributesIntoMap(AppScope::class, binding<Activity>())
@ActivityKey
internal class MainActivity(private val appGraph: AppGraph) : AppCompatActivity() {
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
            intent.isAllowedCategory() &&
            intent.scheme in listOf("file", "content") &&
            intent.type in listOf("application/pdf", "application/zip")
        ) {
            intent.dataString
        } else {
            null
        }

        setContent {
            CompositionLocalProvider(
                LocalMetroViewModelFactory provides appGraph.viewModelFactory,
            ) {
                val state = rememberComicViewerUIState(
                    allowNavigationRestored = receivedBookData.isNullOrEmpty(),
                )
                ComicViewerApp(finishApp = ::finish, state = state)
                LaunchedEffect(receivedBookData.isNullOrEmpty()) {
                    if (!receivedBookData.isNullOrEmpty()) {
                        state.navigator.navigate(ReceiveBookNavKey(receivedBookData))
                        state.onNavigationHistoryRestore()
                    }
                }
            }
        }
    }

    private fun Intent.isAllowedCategory() =
        categories == null || allowedCategories.any { hasCategory(it) }

    private val allowedCategories = listOf(Intent.CATEGORY_BROWSABLE, Intent.CATEGORY_DEFAULT)
}
