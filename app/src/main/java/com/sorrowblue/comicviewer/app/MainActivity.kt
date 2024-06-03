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
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.splashscreen.SplashScreenViewProvider
import androidx.navigation.compose.rememberNavController
import com.ramcosta.composedestinations.utils.toDestinationsNavigator
import com.sorrowblue.comicviewer.app.navgraphs.MainNavGraph
import com.sorrowblue.comicviewer.feature.tutorial.navgraphs.TutorialNavGraph
import dagger.hilt.android.AndroidEntryPoint

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
            val navController = rememberNavController()
            ComicViewerApp(
                onTutorial = {
                    navController.toDestinationsNavigator().navigate(TutorialNavGraph) {
                        popUpTo(MainNavGraph) {
                            inclusive = true
                        }
                    }
                },
                navController = navController
            )
        }
    }
}

private fun SplashScreenViewProvider.startSlideUpAnime() {
    kotlin.runCatching {
        val slideUp = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, 0f, -iconView.height * 2f)
        slideUp.interpolator = AnticipateInterpolator()
        slideUp.doOnEnd { remove() }
        slideUp.duration =
            if (iconAnimationDurationMillis - System.currentTimeMillis() + iconAnimationStartMillis < 0) 300 else iconAnimationDurationMillis - System.currentTimeMillis() + iconAnimationStartMillis
        slideUp.start()
    }.onFailure { remove() }
}
