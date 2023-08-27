package com.sorrowblue.comicviewer.app

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.material.icons.Icons
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.splashscreen.SplashScreenViewProvider
import androidx.core.view.WindowCompat
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
import com.sorrowblue.comicviewer.bookshelf.navigation.ShowNavigationBarBookshelfNavGraph
import com.sorrowblue.comicviewer.bookshelf.navigation.bookshelfGroup
import com.sorrowblue.comicviewer.favorite.navigation.FavoriteGroupRoute
import com.sorrowblue.comicviewer.favorite.navigation.ShowNavigationBarFavoriteNavGraph
import com.sorrowblue.comicviewer.feature.favorite.add.navigation.favoriteAddScreen
import com.sorrowblue.comicviewer.favorite.navigation.favoriteGroup
import com.sorrowblue.comicviewer.feature.favorite.add.navigation.navigateToFavoriteAdd
import com.sorrowblue.comicviewer.feature.search.navigation.navigateToSearch
import com.sorrowblue.comicviewer.feature.search.navigation.searchScreen
import com.sorrowblue.comicviewer.framework.compose.AppMaterialTheme
import com.sorrowblue.comicviewer.framework.compose.FabVisibleState
import com.sorrowblue.comicviewer.readlater.navigation.ReadlaterGroupRoute
import com.sorrowblue.comicviewer.readlater.navigation.ShowNavigationBarReadLaterNavGraph
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
            AppMaterialTheme {
                val snackbarHostState = remember { SnackbarHostState() }
                val navController = rememberNavController()
                val backStackEntry by navController.currentBackStackEntryAsState()

                val fabState = remember { FabVisibleState() }
                Scaffold(
                    bottomBar = {
                        val items2 = remember {
                            ShowNavigationBarBookshelfNavGraph + ShowNavigationBarReadLaterNavGraph + ShowNavigationBarFavoriteNavGraph
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
                                onAddFavoriteClick = navController::navigateToFavoriteAdd,
                            )
                            favoriteGroup(
                                navController,
                                fabState = fabState,
                                onBookClick = navController::navigateToBook,
                                onSettingsClick = navController::navigateToSettings,
                                navigateToSearch = navController::navigateToSearch,
                                onAddFavoriteClick = navController::navigateToFavoriteAdd,
                            )
                            readlaterGroup(
                                navController = navController,
                                onBookClick = navController::navigateToBook,
                                onSettingsClick = navController::navigateToSettings,
                                onAddFavoriteClick = navController::navigateToFavoriteAdd,
                                navigateToSearch = navController::navigateToSearch
                            )
                            searchScreen(navController::popBackStack)
                            settingsScreen()
                            bookScreen(navController)

                            favoriteAddScreen(onBackClick = navController::popBackStack)
                        }
                    }
                }
            }
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
