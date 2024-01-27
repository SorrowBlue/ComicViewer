package com.sorrowblue.comicviewer.app

import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.splashscreen.SplashScreenViewProvider
import androidx.navigation.compose.rememberNavController
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.navigation.popUpTo
import com.sorrowblue.comicviewer.feature.authentication.destinations.AuthenticationScreenDestination
import com.sorrowblue.comicviewer.feature.authentication.navigation.Mode
import com.sorrowblue.comicviewer.feature.tutorial.navigation.TutorialNavGraph
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
                    navController.navigate(TutorialNavGraph) {
                        popUpTo(RootNavGraph) {
                            inclusive = true
                        }
                    }
                },
                onAuth = {
                    navController.navigate(
                        AuthenticationScreenDestination(
                            Mode.Authentication,
                            it
                        )
                    ) {
                        launchSingleTop = true
                        if (it) {
                            popUpTo(RootNavGraph) {
                                inclusive = true
                            }
                        }
                    }
                },
                navController = navController
            )
        }
    }
}
