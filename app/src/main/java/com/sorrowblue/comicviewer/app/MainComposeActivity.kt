package com.sorrowblue.comicviewer.app

import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.splashscreen.SplashScreenViewProvider
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.sorrowblue.comicviewer.app.component.NavHostWithSharedAxisX
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.collections.immutable.toPersistentList
import logcat.logcat

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@AndroidEntryPoint
internal class MainComposeActivity : AppCompatActivity() {

    private val viewModel: ComicViewerAppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen().apply {
            super.onCreate(savedInstanceState)
            setKeepOnScreenCondition(viewModel::shouldKeepOnScreen)
            setOnExitAnimationListener(SplashScreenViewProvider::startSlideUpAnime)
        }

        enableEdgeToEdge(
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        )

        viewModel.restoreComplete()

        setContent {
//            val windowSize = calculateWindowSizeClass(this)
//            ComicViewerApp(windowsSize = windowSize, viewModel = viewModel)
            TestScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Content(
    label: String,
    paddingValues: PaddingValues = PaddingValues(),
    content: @Composable () -> Unit = {},
    click: () -> Unit = {}
) {
    SideEffect { logcat("APPAPP") { "compose $label" } }
    val layoutDirection = LocalLayoutDirection.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = label) }, scrollBehavior = scrollBehavior)
        },
        contentWindowInsets = WindowInsets(
            left = paddingValues.calculateLeftPadding(layoutDirection),
            right = paddingValues.calculateRightPadding(layoutDirection),
            top = paddingValues.calculateTopPadding(),
            bottom = paddingValues.calculateBottomPadding(),
        ),
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) {
        val list = remember { (1..20).map { "$label $it" }.toPersistentList() }
        Surface(modifier = Modifier.padding(it)) {
            LazyColumn {
                items(list, key = { it }) {
                    ListItem(headlineContent = { Text(it) }, Modifier.clickable(onClick = click))
                }
            }
            content()
        }
    }
}

@Composable
fun TestScreen() {

    MaterialTheme {

        val navController = rememberNavController()

        val stackEntry by navController.currentBackStackEntryAsState()

        var visible by remember { mutableStateOf(true) }
        val menus = remember {
            listOf("A", "B", "C")
        }

        LaunchedEffect(stackEntry) {
            logcat { "stackEntry?.destination = ${stackEntry?.destination?.route}" }
            visible = stackEntry?.destination?.route in menus
        }

        val toD = remember {
            {
                navController.navigate("D")
            }
        }

        val clickNav = remember {
            { route: String ->
                navController.navigate(
                    route,
                    navOptions {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                )
            }
        }


        logcat("APPAPP") { "MaterialTheme" }
        Scaffold(
            bottomBar = {
//                        AnimatedVisibility(visible = visible, enter = slideInVertically { it }, exit = slideOutVertically { it }) {

                AnimatedContent(
                    targetState = visible,
                    transitionSpec = { slideInVertically { height -> height } togetherWith slideOutVertically { height -> height } },
                    label = "test"
                ) { isVisible ->
                    if (isVisible) {
                        NavigationBar {
                            menus.forEach { s ->
                                NavigationBarItem(
                                    selected = s == stackEntry?.destination?.route,
                                    onClick = { clickNav(s) },
                                    icon = { },
                                    label = { Text(text = s) }
                                )
                            }
                        }
                    } else {
                        Box(modifier = Modifier.fillMaxWidth())
                    }
                }
            }) { contentPadding ->
            NavHostWithSharedAxisX(navController = navController, startDestination = "A") {
                logcat("APPAPP") { "navhost root" }

                composable("A") {
                    Content(label = "A", contentPadding, click = toD)
                }
                composable("B") {
                    Content(label = "B", contentPadding, content = {
                        Box(modifier = Modifier.fillMaxSize()) {
                            FloatingActionButton(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(end = 16.dp, bottom = 16.dp),
                                onClick = { navController.navigate("D") }) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null
                                )
                            }
                        }
                    })
                }
                composable("C") {
                    Content(label = "C", contentPadding) {
                    }
                }
                composable("D") {
                    Content(label = "D", contentPadding) {
                    }
                }
            }
        }

    }
}
