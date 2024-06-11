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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.SaveableStateRegistry
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.splashscreen.SplashScreenViewProvider
import com.ramcosta.composedestinations.DestinationsNavHost
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.multi.MultiUseCase
import com.sorrowblue.comicviewer.multi.RestoableItem
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.random.Random
import logcat.logcat

@AndroidEntryPoint
internal class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var multiUseCase: MultiUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().apply {
            enableEdgeToEdge(
                navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
            )
            super.onCreate(savedInstanceState)
            setOnExitAnimationListener(SplashScreenViewProvider::startShrinkingAnimation)
            setKeepOnScreenCondition(viewModel.shouldKeepSplash::value)
        }

        setContent {
            var restoableItem by rememberSaveable {
                mutableStateOf(RestoableItem(0, ""))
            }
            ComicTheme {
                DestinationsNavHost(navGraph = NavGraphs.root)
                Scaffold {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(it),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = restoableItem.count.toString())

                        Button(onClick = {
                            restoableItem = restoableItem.copy(restoableItem.count + 1)
                        }) {
                            Text(text = "count up")
                        }
                        Text(text = restoableItem.name)
                        Button(onClick = {
                            restoableItem = restoableItem.copy(name = Random.nextLong().toString())
                        }) {
                            Text(text = "name change")
                        }
                    }
                }
            }
        }

        logcat { "UUID=${multiUseCase.getUuid()}, ${multiUseCase.hashCode()}" }
    }
}

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
