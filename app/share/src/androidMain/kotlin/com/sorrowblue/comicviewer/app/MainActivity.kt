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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.sorrowblue.comicviewer.app.ComicViewerAppViewModel.Factory
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
internal class MainActivity(private val metroViewModelFactory: MetroViewModelFactory) :
    AppCompatActivity() {

    override val defaultViewModelProviderFactory: ViewModelProvider.Factory by lazy {
        val superFactory = super.defaultViewModelProviderFactory
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T = runCatching {
                metroViewModelFactory.create(modelClass)
            }.getOrElse { superFactory.create(modelClass) }

            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T =
                runCatching {
                    metroViewModelFactory.create(modelClass, extras)
                }.getOrElse {
                    superFactory.create(modelClass, extras)
                }
        }
    }

    private val viewModel: ComicViewerAppViewModel by metroViewModel { factory: Factory ->
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

    private inline fun <reified T : ManualViewModelAssistedFactory, reified VM : ViewModel> metroViewModel(
        crossinline creationCallback: (T) -> VM,
    ): Lazy<VM> = viewModels<VM> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val factory = metroViewModelFactory
                @Suppress("UNCHECKED_CAST")
                return creationCallback(
                    factory.createManuallyAssistedFactory(T::class).invoke(),
                ) as T
            }

            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val factory = metroViewModelFactory
                @Suppress("UNCHECKED_CAST")
                return creationCallback(
                    factory.createManuallyAssistedFactory(T::class).invoke(),
                ) as T
            }
        }
    }
}
