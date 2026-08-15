/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.app

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.LaunchedEffect
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.splashscreen.SplashScreenViewProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sorrowblue.comicviewer.feature.book.navigation.ReceiveBookNavKey
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.android.ActivityKey
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.MetroViewModelFactory

/**
 * Main activity
 */
@ContributesIntoMap(AppScope::class, binding<Activity>())
@ActivityKey
internal class MainActivity(override val defaultViewModelProviderFactory: MetroViewModelFactory) :
    AppCompatActivity() {

    private val viewModel: ComicViewerAppViewModel by metroViewModel { factory: ComicViewerAppViewModel.Factory, ->
        factory.create(receivedBookData.isNullOrEmpty())
    }

    val receivedBookData
        get() = if (intent.action == Intent.ACTION_VIEW &&
            intent.isAllowedCategory() &&
            intent.scheme in listOf("file", "content") &&
            intent.type in listOf("application/pdf", "application/zip")
        ) {
            intent.dataString
        } else {
            null
        }

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
                ComicViewerApp(
                    finishApp = ::finish,
                    navigator = navigator,
                    allowNavigationRestored = receivedBookData.isNullOrEmpty(),
                )
                LaunchedEffect(receivedBookData.isNullOrEmpty()) {
                    receivedBookData?.let { data ->
                        if (data.isNotEmpty()) {
                            navigator.navigate(ReceiveBookNavKey(data))
                            viewModel.completeInit()
                        }
                    }
                }
            }
        }
    }

    private fun Intent.isAllowedCategory() =
        categories == null || allowedCategories.any { hasCategory(it) }

    private val allowedCategories = listOf(Intent.CATEGORY_BROWSABLE, Intent.CATEGORY_DEFAULT)
}

private inline fun <reified T : ManualViewModelAssistedFactory, reified VM : ViewModel> ComponentActivity.metroViewModel(
    crossinline creationCallback: (T) -> VM,
): Lazy<VM> = viewModels<VM> {
    object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val factory =
                this@metroViewModel.defaultViewModelProviderFactory as MetroViewModelFactory
            @Suppress("UNCHECKED_CAST")
            return creationCallback(factory.createManuallyAssistedFactory(T::class).invoke()) as T
        }
    }
}
