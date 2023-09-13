package com.sorrowblue.comicviewer.app

import android.app.Activity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Book
import androidx.compose.material.icons.twotone.Favorite
import androidx.compose.material.icons.twotone.LibraryBooks
import androidx.compose.material.icons.twotone.WatchLater
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.google.android.play.core.review.ReviewManagerFactory
import com.mikepenz.aboutlibraries.LibsBuilder
import com.sorrowblue.comicviewer.bookshelf.navigation.bookshelfGraphRoute
import com.sorrowblue.comicviewer.bookshelf.navigation.bookshelfGroup
import com.sorrowblue.comicviewer.bookshelf.navigation.routeInBookshelfGraph
import com.sorrowblue.comicviewer.domain.AddOn
import com.sorrowblue.comicviewer.favorite.navigation.FavoriteFolderRoute
import com.sorrowblue.comicviewer.favorite.navigation.FavoriteGroupRoute
import com.sorrowblue.comicviewer.favorite.navigation.FavoriteListRoute
import com.sorrowblue.comicviewer.favorite.navigation.FavoriteRoute
import com.sorrowblue.comicviewer.favorite.navigation.favoriteGroup
import com.sorrowblue.comicviewer.feature.book.navigation.BookRoute
import com.sorrowblue.comicviewer.feature.book.navigation.bookScreen
import com.sorrowblue.comicviewer.feature.book.navigation.navigateToBook
import com.sorrowblue.comicviewer.feature.bookshelf.edit.navigation.bookshelfEditScreen
import com.sorrowblue.comicviewer.feature.bookshelf.edit.navigation.navigateToBookshelfEdit
import com.sorrowblue.comicviewer.feature.bookshelf.selection.navigation.bookshelfSelectionRoute
import com.sorrowblue.comicviewer.feature.bookshelf.selection.navigation.bookshelfSelectionScreen
import com.sorrowblue.comicviewer.feature.bookshelf.selection.navigation.navigateToBookshelfSelection
import com.sorrowblue.comicviewer.feature.favorite.add.navigation.favoriteAddScreen
import com.sorrowblue.comicviewer.feature.favorite.add.navigation.navigateToFavoriteAdd
import com.sorrowblue.comicviewer.feature.history.navigation.HistoryFolderRoute
import com.sorrowblue.comicviewer.feature.history.navigation.HistoryRoute
import com.sorrowblue.comicviewer.feature.library.navigation.LibraryGroupRoute
import com.sorrowblue.comicviewer.feature.library.navigation.LibraryRoute
import com.sorrowblue.comicviewer.feature.library.navigation.libraryGroup
import com.sorrowblue.comicviewer.feature.library.serviceloader.AddOnNavigation
import com.sorrowblue.comicviewer.feature.library.serviceloader.BoxNavigation
import com.sorrowblue.comicviewer.feature.library.serviceloader.DropBoxNavigation
import com.sorrowblue.comicviewer.feature.library.serviceloader.GoogleDriveNavigation
import com.sorrowblue.comicviewer.feature.library.serviceloader.OneDriveNavigation
import com.sorrowblue.comicviewer.feature.readlater.navigation.ReadLaterFolderRoute
import com.sorrowblue.comicviewer.feature.readlater.navigation.ReadLaterRoute
import com.sorrowblue.comicviewer.feature.readlater.navigation.ReadlaterGroupRoute
import com.sorrowblue.comicviewer.feature.readlater.navigation.readlaterGroup
import com.sorrowblue.comicviewer.feature.search.navigation.navigateToSearch
import com.sorrowblue.comicviewer.feature.search.navigation.searchScreen
import com.sorrowblue.comicviewer.feature.settings.navigation.navigateToSettings
import com.sorrowblue.comicviewer.feature.settings.navigation.settingsNavGraph
import com.sorrowblue.comicviewer.feature.tutorial.navigation.TutorialRoute
import com.sorrowblue.comicviewer.feature.tutorial.navigation.navigateToTutorial
import com.sorrowblue.comicviewer.feature.tutorial.navigation.tutorialScreen
import com.sorrowblue.comicviewer.framework.compose.AppMaterialTheme
import com.sorrowblue.comicviewer.framework.compose.CollectAsEffect
import com.sorrowblue.comicviewer.framework.compose.LifecycleEffect
import com.sorrowblue.comicviewer.framework.compose.LocalWindowSize
import java.util.ServiceLoader
import kotlinx.collections.immutable.PersistentList
import logcat.asLog
import logcat.logcat

sealed interface ComicViewerAppUiEvent {
    data object StartTutorial : ComicViewerAppUiEvent
    data class CompleteTutorial(val isInitial: Boolean) : ComicViewerAppUiEvent
}

@Composable
fun ComicViewerApp(
    windowsSize: WindowSizeClass,
    viewModel: ComicViewerAppViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    AppMaterialTheme {
        CompositionLocalProvider(LocalWindowSize provides windowsSize) {
            Surface(
                modifier = modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background,
            ) {
                val addOnList by viewModel.addOnList.collectAsState()
                ComicViewerNavHost(
                    windowsSize = windowsSize,
                    navController = navController,
                    addOnList = addOnList,
                    onTutorialComplete = viewModel::completeTutorial
                )
            }
        }
    }
    viewModel.uiEvents.CollectAsEffect {
        when (it) {
            ComicViewerAppUiEvent.StartTutorial -> navController.navigateToTutorial(
                navOptions {
                    popUpTo(mainScreenRoute) {
                        inclusive = true
                    }
                }
            )

            is ComicViewerAppUiEvent.CompleteTutorial ->
                if (it.isInitial) {
                    navController.navigate(
                        mainScreenRoute,
                        navOptions {
                            popUpTo(TutorialRoute) {
                                inclusive = true
                            }
                        }
                    )
                } else {
                    navController.popBackStack()
                }
        }
    }
    LifecycleEffect(lifecycleObserver = viewModel)
}

@Composable
fun ComicViewerNavHost(
    windowsSize: WindowSizeClass,
    navController: NavHostController,
    addOnList: PersistentList<AddOn>,
    onTutorialComplete: () -> Unit
) {
    val context = LocalContext.current
    NavHostWithSharedAxisX(navController = navController, startDestination = mainScreenRoute) {
        mainScreen(windowsSize, navController)

        bookshelfSelectionScreen(
            onBackClick = navController::popBackStack,
            onSourceClick = navController::navigateToBookshelfEdit
        )
        bookshelfEditScreen(
            onBackClick = navController::popBackStack,
            onComplete = { navController.popBackStack(bookshelfSelectionRoute, true) }
        )

        searchScreen(navController::popBackStack)
        settingsNavGraph(
            navController = navController,
            onLicenceClick = {
                LibsBuilder().withActivityTitle("Licence").withSearchEnabled(true)
                    .withEdgeToEdge(true).start(context)
            },
            onRateAppClick = {
                val manager = ReviewManagerFactory.create(context)
                manager.requestReviewFlow().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        manager.launchReviewFlow(context as Activity, task.result)
                            .addOnCompleteListener { a ->
                                if (a.isSuccessful) {
                                    logcat { "成功" }
                                } else {
                                    logcat { a.exception?.asLog().toString() }
                                    CustomTabsIntent.Builder().build().launchUrl(
                                        context,
                                        "http://play.google.com/store/apps/details?id=${context.packageName}".toUri()
                                    )
                                }
                            }
                    } else {
                        logcat { task.exception?.asLog().toString() }
                        CustomTabsIntent.Builder().build().launchUrl(
                            context,
                            "http://play.google.com/store/apps/details?id=${context.packageName}".toUri()
                        )
                    }
                }
            },
            onStartTutorialClick = navController::navigateToTutorial
        )
        tutorialScreen(
            onComplete = onTutorialComplete
        )
        bookScreen(
            onBackClick = navController::popBackStack,
            onNextBookClick = {
                navController.navigateToBook(it.bookshelfId, it.path, navOptions = navOptions {
                    popUpTo("$BookRoute/{bookshelfId}/{path}?favoriteId={favoriteId}") {
                        inclusive = true
                    }
                })
            })

        favoriteAddScreen(onBackClick = navController::popBackStack)

        addOnList.forEach {
            with(it.loadDynamicFeature() ?: return@forEach) {
                addOnScreen(navController)
            }
        }
    }
}

private fun AddOn.loadDynamicFeature(): AddOnNavigation? {
    return when (this) {
        AddOn.Document -> null
        AddOn.GoogleDrive -> ServiceLoader.load(
            GoogleDriveNavigation.Provider::class.java,
            GoogleDriveNavigation.Provider::class.java.classLoader
        ).iterator().next().get()

        AddOn.OneDrive -> ServiceLoader.load(
            OneDriveNavigation.Provider::class.java,
            OneDriveNavigation.Provider::class.java.classLoader
        ).iterator().next().get()

        AddOn.Dropbox -> ServiceLoader.load(
            DropBoxNavigation.Provider::class.java,
            DropBoxNavigation.Provider::class.java.classLoader
        ).iterator().next().get()

        AddOn.Box -> ServiceLoader.load(
            BoxNavigation.Provider::class.java,
            BoxNavigation.Provider::class.java.classLoader
        ).iterator().next().get()
    }
}


private fun NavGraphBuilder.mainScreen(
    windowSize: WindowSizeClass,
    navController: NavHostController,
) {
    mainScreen(
        windowSize = windowSize,
        mainNestedGraphStateHolder = ComicViewerAppMainNestedGraphStateHolder(),
        mainNestedGraph = { mainNestedNavController, contentPadding ->
            bookshelfGroup(
                contentPadding = contentPadding,
                navController = mainNestedNavController,
                onSettingsClick = navController::navigateToSettings,
                navigateToBook = navController::navigateToBook,
                navigateToSearch = navController::navigateToSearch,
                onAddFavoriteClick = navController::navigateToFavoriteAdd,
                onEditClick = navController::navigateToBookshelfEdit,
                onAddClick = navController::navigateToBookshelfSelection
            )
            favoriteGroup(
                contentPadding = contentPadding,
                navController = mainNestedNavController,
                onBookClick = navController::navigateToBook,
                onSettingsClick = navController::navigateToSettings,
                navigateToSearch = navController::navigateToSearch,
                onAddFavoriteClick = navController::navigateToFavoriteAdd,
            )
            readlaterGroup(
                contentPadding = contentPadding,
                navController = mainNestedNavController,
                onBookClick = navController::navigateToBook,
                onSettingsClick = navController::navigateToSettings,
                onAddFavoriteClick = navController::navigateToFavoriteAdd,
                navigateToSearch = navController::navigateToSearch
            )
            libraryGroup(
                contentPadding = contentPadding,
                navController = mainNestedNavController,
                onBookClick = navController::navigateToBook,
                onSettingsClick = navController::navigateToSettings,
                onAddFavoriteClick = navController::navigateToFavoriteAdd,
                navigateToSearch = navController::navigateToSearch,
                onAddOnClick = { addOn ->
                    addOn.addOn.loadDynamicFeature()?.let {
                        with(it) {
                            navController.navigateToAddOnScreen()
                        }
                    }
                }
            )
        },
    )
}

class ComicViewerAppMainNestedGraphStateHolder : MainNestedGraphStateHolder {
    override val startDestination: String = bookshelfGraphRoute

    override fun routeToTab(route: String): MainScreenTab {
        return when (route) {
            in routeInBookshelfGraph -> MainScreenTab.Bookshelf
            FavoriteListRoute, FavoriteRoute, FavoriteFolderRoute -> MainScreenTab.Favorite
            ReadLaterRoute, ReadLaterFolderRoute -> MainScreenTab.Readlater
            LibraryRoute, HistoryRoute, HistoryFolderRoute -> MainScreenTab.Library
            else -> MainScreenTab.Bookshelf
        }
    }

    override fun onTabSelected(
        navController: NavController,
        tab: MainScreenTab,
    ) {
        when (tab) {
            MainScreenTab.Bookshelf -> bookshelfGraphRoute
            MainScreenTab.Favorite -> FavoriteGroupRoute
            MainScreenTab.Readlater -> ReadlaterGroupRoute
            MainScreenTab.Library -> LibraryGroupRoute
        }.let { route ->
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
}

enum class MainScreenTab(
    val label: Int,
    val icon: ImageVector,
    val contentDescription: Int
) {

    Bookshelf(R.string.app_label_bookshelf, Icons.TwoTone.Book, R.string.app_label_bookshelf),
    Favorite(R.string.app_label_favorite, Icons.TwoTone.Favorite, R.string.app_label_favorite),
    Readlater(
        R.string.app_label_read_later,
        Icons.TwoTone.WatchLater,
        R.string.app_label_read_later
    ),
    Library(R.string.app_label_library, Icons.TwoTone.LibraryBooks, R.string.app_label_library),

}
