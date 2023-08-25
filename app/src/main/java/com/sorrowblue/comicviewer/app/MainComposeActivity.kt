package com.sorrowblue.comicviewer.app

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material.icons.twotone.Book
import androidx.compose.material.icons.twotone.Favorite
import androidx.compose.material.icons.twotone.LibraryBooks
import androidx.compose.material.icons.twotone.WatchLater
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.splashscreen.SplashScreenViewProvider
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.google.android.material.color.DynamicColors
import com.sorrowblue.comicviewer.book.compose.bookScreen
import com.sorrowblue.comicviewer.book.compose.navigateToBook
import com.sorrowblue.comicviewer.bookshelf.navigation.BookshelfGroupRoute
import com.sorrowblue.comicviewer.bookshelf.navigation.BookshelfRoute
import com.sorrowblue.comicviewer.bookshelf.navigation.bookshelfGroup
import com.sorrowblue.comicviewer.bookshelf.navigation.navigateToBookshelfSelection
import com.sorrowblue.comicviewer.favorite.navigation.FavoriteGroupRoute
import com.sorrowblue.comicviewer.favorite.navigation.FavoriteRoute
import com.sorrowblue.comicviewer.favorite.navigation.favoriteGroup
import com.sorrowblue.comicviewer.feature.search.navigation.navigateToSearch
import com.sorrowblue.comicviewer.feature.search.navigation.searchScreen
import com.sorrowblue.comicviewer.folder.navigation.folderRoute
import com.sorrowblue.comicviewer.framework.compose.FabVisibleState
import com.sorrowblue.comicviewer.readlater.navigation.ReadLaterRoute
import com.sorrowblue.comicviewer.readlater.navigation.ReadlaterGroupRoute
import com.sorrowblue.comicviewer.readlater.navigation.readlaterGroup
import com.sorrowblue.comicviewer.settings.navigateToSettings
import com.sorrowblue.comicviewer.settings.settingsScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainComposeActivity : AppCompatActivity() {

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen().apply {
            DynamicColors.applyToActivityIfAvailable(this@MainComposeActivity)
            super.onCreate(savedInstanceState)
            setOnExitAnimationListener(SplashScreenViewProvider::startSlideUpAnime)
            setKeepOnScreenCondition {
                false
            }
        }
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val snackbarHostState = remember { SnackbarHostState() }
            val navController = rememberNavController()
            val backStackEntry by navController.currentBackStackEntryAsState()

            val fabState = remember { FabVisibleState() }
            Scaffold(
                bottomBar = {
                    val items2 = remember {
                        listOf(
                            BookshelfGroupRoute,
                            BookshelfRoute,
                            folderRoute(BookshelfRoute),
                            ReadLaterRoute,
                            folderRoute(ReadLaterRoute),
                            FavoriteRoute,
                            folderRoute(FavoriteRoute),
                        )
                    }
                    if (items2.any { it == backStackEntry?.destination?.route }) {
                        val items = remember {
                            listOf(
                                BottomMenu.Bookshelf,
                                BottomMenu.Favorite,
                                BottomMenu.Readlater,
                                BottomMenu.Library
                            )
                        }
                        NavigationBar {
                            items.forEach { item ->
                                val selected =
                                    backStackEntry?.destination?.hierarchy?.any { it.route == item.route } == true
                                NavigationBarItem(
                                    icon = { Icon(item.icon, null) },
                                    label = { Text(stringResource(item.label)) },
                                    selected = selected,
                                    onClick = {
                                        navController.navigate(
                                            item.route,
                                            navOptions {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        )
                                    }
                                )
                            }
                        }
                    }
                },
                floatingActionButton = {
                    AnimatedVisibility(fabState.state, enter = scaleIn(), exit = scaleOut()) {
                        FloatingActionButton(onClick = { fabState.onClick() }) {
                            Icon(fabState.icon!!, null)
                        }
                    }
                },
                snackbarHost = {
                    SnackbarHost(snackbarHostState) { data ->
                        Snackbar {
                            Text(data.visuals.message)
                        }
                    }
                }
            ) {
                NavHost(
                    navController = navController,
                    startDestination = "Parent"
                ) {
                    navigation(startDestination = "bookshelf_group", route = "Parent") {
                        bookshelfGroup(
                            navController,
                            fabState = fabState,
                            onSettingsClick = navController::navigateToSettings,
                            navigateToBook = navController::navigateToBook,
                            navigateToSearch = navController::navigateToSearch,
                            onAddFavoriteClick = { TODO() },
                        )
                        favoriteGroup(
                            navController,
                            onSettingsClick = navController::navigateToSettings,
                            fabState = fabState
                        )
                        readlaterGroup(
                            navController = navController,
                            onBookClick = navController::navigateToBook,
                            onSettingsClick = navController::navigateToSettings,
                            onAddFavoriteClick = { TODO() },
                            navigateToSearch = navController::navigateToSearch
                        )
                        searchScreen(navController::popBackStack)
                        settingsScreen()
                        bookScreen(navController)
                    }
                }
            }
        }
    }
}

sealed interface FabItem {

    val currentRoute: String

    fun onClick(navController: NavController)

    @Composable
    fun Content()

    data object Bookshelf : FabItem {
        override val currentRoute = BookshelfRoute
        override fun onClick(navController: NavController) {
            navController.navigateToBookshelfSelection()
        }

        @Composable
        override fun Content() {
            Icon(Icons.TwoTone.Add, null)
        }
    }

    data object Favorite : FabItem {
        override val currentRoute = FavoriteRoute
        override fun onClick(navController: NavController) {
            TODO()
        }

        @Composable
        override fun Content() {
            Icon(Icons.TwoTone.Add, null)
        }
    }
}


sealed interface BottomMenu {
    val route: String
    val label: Int
    val icon: ImageVector

    data object Bookshelf : BottomMenu {
        override val route: String = BookshelfGroupRoute
        override val label = R.string.app_label_bookshelf
        override val icon = Icons.TwoTone.Book
    }

    data object Favorite : BottomMenu {
        override val route = FavoriteGroupRoute
        override val label = R.string.app_label_favorite
        override val icon = Icons.TwoTone.Favorite
    }

    data object Readlater : BottomMenu {
        override val route = ReadlaterGroupRoute
        override val label = R.string.app_label_read_later
        override val icon = Icons.TwoTone.WatchLater
    }

    data object Library : BottomMenu {
        override val route = "library"
        override val label = R.string.app_label_library
        override val icon = Icons.TwoTone.LibraryBooks
    }
}

@Composable
private fun FloatingActionButton(navController: NavController, fabItem: FabItem?) {
    FloatingActionButton(
        modifier = Modifier.padding(16.dp),
        onClick = {
            fabItem?.onClick(navController)
        }) {
        fabItem?.Content()
    }
}
