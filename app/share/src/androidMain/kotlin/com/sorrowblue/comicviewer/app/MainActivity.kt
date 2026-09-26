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
import androidx.compose.runtime.LaunchedEffect
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.splashscreen.SplashScreenViewProvider
import androidx.lifecycle.ViewModelProvider
import com.sorrowblue.comicviewer.feature.book.navigation.ReceiveBookNavKey
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.android.ActivityKey

/**
 * Main activity
 */
@ContributesIntoMap(AppScope::class, binding<Activity>())
@ActivityKey
internal class MainActivity(private val viewModelFactory: ViewModelProvider.Factory) :
    AppCompatActivity() {

    private val viewModel by viewModels<ComicViewerAppViewModel>()

    private val receivedBookData
        get() = if (intent.action == Intent.ACTION_VIEW &&
            intent.isAllowedCategory() &&
            intent.scheme in listOf("file", "content") &&
            intent.type in listOf("application/pdf", "application/zip")
        ) {
            intent.dataString
        } else {
            null
        }

    override val defaultViewModelProviderFactory get() = viewModelFactory

    @Suppress("UnnecessaryLaunchedEffect")
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().apply {
            enableEdgeToEdge(
                navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
            )
            super.onCreate(savedInstanceState)
            setOnExitAnimationListener(SplashScreenViewProvider::startShrinkingAnimation)
            setKeepOnScreenCondition(viewModel.shouldKeepSplash::value)
        }

        setContent {
            MetroContent {
                val navigator = rememberAppNavigator()
                val bookData = receivedBookData
                ComicViewerApp(
                    finishApp = ::finish,
                    navigator = navigator,
                    allowNavigationRestored = bookData.isNullOrEmpty(),
                )
                LaunchedEffect(bookData) {
                    if (!bookData.isNullOrEmpty()) {
                        navigator.navigate(ReceiveBookNavKey(bookData))
                        viewModel.completeNavigationRestore()
                    }
                }
            }
        }
    }

    private fun Intent.isAllowedCategory() =
        categories == null || allowedCategories.any { hasCategory(it) }

    private val allowedCategories = listOf(Intent.CATEGORY_BROWSABLE, Intent.CATEGORY_DEFAULT)
}
