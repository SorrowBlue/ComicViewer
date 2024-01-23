package com.sorrowblue.comicviewer.app

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.splashscreen.SplashScreenViewProvider
import dagger.hilt.android.AndroidEntryPoint
import logcat.logcat

@AndroidEntryPoint
internal class MainActivity : AppCompatActivity() {

    private val viewModel: ComicViewerAppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().apply {
            enableEdgeToEdge(
                navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
            )
            super.onCreate(savedInstanceState)
            setKeepOnScreenCondition(viewModel::shouldKeepSplash)
            setOnExitAnimationListener(SplashScreenViewProvider::startSlideUpAnime)
        }


        setContent {
            ComicViewerApp()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            onBackPressedDispatcher.addCallback(
                this,
                object : OnBackPressedCallback(false) {
                    override fun handleOnBackPressed() {
                        logcat { "onback" }
                    }
                }
            )
        }
    }
}
