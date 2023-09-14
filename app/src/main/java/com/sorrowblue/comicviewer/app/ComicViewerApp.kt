package com.sorrowblue.comicviewer.app

import android.app.Activity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
import com.sorrowblue.comicviewer.app.component.NavHostWithSharedAxisX
import com.sorrowblue.comicviewer.bookshelf.navigation.bookshelfGraphRoute
import com.sorrowblue.comicviewer.bookshelf.navigation.bookshelfGroup
import com.sorrowblue.comicviewer.bookshelf.navigation.navigateToBookshelfFolder
import com.sorrowblue.comicviewer.bookshelf.navigation.routeInBookshelfGraph
import com.sorrowblue.comicviewer.domain.AddOn
import com.sorrowblue.comicviewer.domain.usecase.NavigationHistory
import com.sorrowblue.comicviewer.favorite.navigation.favoriteGraphRoute
import com.sorrowblue.comicviewer.favorite.navigation.favoriteGroup
import com.sorrowblue.comicviewer.favorite.navigation.routeInFavoriteGraph
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
import com.sorrowblue.comicviewer.feature.favorite.edit.navigation.favoriteEditScreen
import com.sorrowblue.comicviewer.feature.favorite.edit.navigation.navigateToFavoriteEdit
import com.sorrowblue.comicviewer.feature.library.navigation.libraryGraphRoute
import com.sorrowblue.comicviewer.feature.library.navigation.libraryGroup
import com.sorrowblue.comicviewer.feature.library.navigation.routeInLibraryGraph
import com.sorrowblue.comicviewer.feature.library.serviceloader.AddOnNavigation
import com.sorrowblue.comicviewer.feature.library.serviceloader.BoxNavigation
import com.sorrowblue.comicviewer.feature.library.serviceloader.DropBoxNavigation
import com.sorrowblue.comicviewer.feature.library.serviceloader.GoogleDriveNavigation
import com.sorrowblue.comicviewer.feature.library.serviceloader.OneDriveNavigation
import com.sorrowblue.comicviewer.feature.main.MainNestedGraphStateHolder
import com.sorrowblue.comicviewer.feature.main.MainScreenTab
import com.sorrowblue.comicviewer.feature.main.mainRoute
import com.sorrowblue.comicviewer.feature.main.mainScreen
import com.sorrowblue.comicviewer.feature.readlater.navigation.readlaterGraphRoute
import com.sorrowblue.comicviewer.feature.readlater.navigation.readlaterGroup
import com.sorrowblue.comicviewer.feature.readlater.navigation.routeInReadlaterGraph
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
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

internal sealed interface ComicViewerAppUiEvent {
    data object StartTutorial : ComicViewerAppUiEvent
    data class CompleteTutorial(val isInitial: Boolean) : ComicViewerAppUiEvent
}

@Composable
internal fun ComicViewerApp(
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
                val history by viewModel.history.collectAsState()
                ComicViewerNavHost(
                    windowsSize = windowsSize,
                    navController = navController,
                    addOnList = addOnList,
                    onTutorialComplete = viewModel::completeTutorial,
                    history = history,
                    restoreComplete = viewModel::restoreComplete
                )
            }
        }
    }
    viewModel.uiEvents.CollectAsEffect {
        when (it) {
            ComicViewerAppUiEvent.StartTutorial -> navController.navigateToTutorial(
                navOptions {
                    popUpTo(mainRoute) {
                        inclusive = true
                    }
                }
            )

            is ComicViewerAppUiEvent.CompleteTutorial ->
                if (it.isInitial) {
                    navController.navigate(
                        mainRoute,
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
private fun ComicViewerNavHost(
    windowsSize: WindowSizeClass,
    navController: NavHostController,
    addOnList: PersistentList<AddOn>,
    onTutorialComplete: () -> Unit,
    history: NavigationHistory?,
    restoreComplete: () -> Unit
) {
    val context = LocalContext.current
    NavHostWithSharedAxisX(navController = navController, startDestination = mainRoute) {
        mainScreen(windowsSize, navController, history = history, restoreComplete = restoreComplete)

        bookshelfSelectionScreen(
            onBackClick = navController::popBackStack,
            onSourceClick = navController::navigateToBookshelfEdit
        )
        bookshelfEditScreen(
            onBackClick = navController::popBackStack,
            onComplete = { navController.popBackStack(bookshelfSelectionRoute, true) }
        )

        favoriteAddScreen(onBackClick = navController::popBackStack)
        favoriteEditScreen(
            onBackClick = navController::popBackStack,
            onComplete = navController::popBackStack
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
    history: NavigationHistory?,
    restoreComplete: () -> Unit
) {
    mainScreen(
        windowSize = windowSize,
        mainNestedGraphStateHolder = ComicViewerAppMainNestedGraphStateHolder(),
        mainNestedGraph = { mainNestedNavController, contentPadding ->
            bookshelfGroup(
                contentPadding = contentPadding,
                navController = mainNestedNavController,
                onSettingsClick = navController::navigateToSettings,
                navigateToBook = { id, path, pos ->
                    navController.navigateToBook(id, path, position = pos)
                },
                navigateToSearch = navController::navigateToSearch,
                onAddFavoriteClick = navController::navigateToFavoriteAdd,
                onEditClick = navController::navigateToBookshelfEdit,
                onAddClick = navController::navigateToBookshelfSelection,
                onRestoreComplete = restoreComplete
            )
            favoriteGroup(
                contentPadding = contentPadding,
                navController = mainNestedNavController,
                onBookClick = { id, path, pos ->
                    navController.navigateToBook(id, path, position = pos)
                },
                onSettingsClick = navController::navigateToSettings,
                navigateToSearch = navController::navigateToSearch,
                onAddFavoriteClick = navController::navigateToFavoriteAdd,
                onEditClick = navController::navigateToFavoriteEdit
            )
            readlaterGroup(
                contentPadding = contentPadding,
                navController = mainNestedNavController,
                onBookClick = { id, path, pos ->
                    navController.navigateToBook(id, path, position = pos)
                },
                onSettingsClick = navController::navigateToSettings,
                onAddFavoriteClick = navController::navigateToFavoriteAdd,
                navigateToSearch = navController::navigateToSearch
            )
            libraryGroup(
                contentPadding = contentPadding,
                navController = mainNestedNavController,
                onBookClick = { id, path, pos ->
                    navController.navigateToBook(id, path, position = pos)
                },
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
            if (history != null) {
                val (bookshelf, folders, position) = history.triple
                logcat("RESTORE_NAVIGATION", LogPriority.INFO) { "Start restore navigation." }
                if (folders.isNotEmpty()) {
                    // library -> folder
                    if (folders.size == 1) {
                        mainNestedNavController.navigateToBookshelfFolder(
                            bookshelf.id,
                            folders.first().path,
                            position
                        )
                        logcat("RESTORE_NAVIGATION", LogPriority.INFO) {
                            "bookshelf(${bookshelf.id}) -> folder(${folders.first().path})"
                        }
                    } else {
                        mainNestedNavController.navigateToBookshelfFolder(
                            bookshelf.id,
                            folders.first().path
                        )
                        logcat("RESTORE_NAVIGATION", LogPriority.INFO) {
                            "bookshelf(${bookshelf.id}) -> folder(${folders.first().path})"
                        }
                        folders.drop(1).dropLast(1).forEachIndexed { index, folder ->
                            // folder -> folder
                            mainNestedNavController.navigateToBookshelfFolder(
                                bookshelf.id,
                                folder.path
                            )
                            logcat("RESTORE_NAVIGATION", LogPriority.INFO) {
                                "folder(${folders[index].path}) -> folder${folders[index + 1].path}"
                            }
                        }
                        mainNestedNavController.navigateToBookshelfFolder(
                            bookshelf.id,
                            folders.last().path,
                            position
                        )
                        logcat("RESTORE_NAVIGATION", LogPriority.INFO) {
                            "folder(${
                                folders.dropLast(1).last().path
                            }) -> folder${folders.last().path}"
                        }
                    }
                }
                restoreComplete()
            }
        },
    )
}

private class ComicViewerAppMainNestedGraphStateHolder : MainNestedGraphStateHolder {
    override val startDestination: String = bookshelfGraphRoute

    override fun routeToTab(route: String): MainScreenTab {
        return when (route) {
            in routeInBookshelfGraph -> MainScreenTab.Bookshelf
            in routeInFavoriteGraph -> MainScreenTab.Favorite
            in routeInReadlaterGraph -> MainScreenTab.Readlater
            in routeInLibraryGraph -> MainScreenTab.Library
            else -> MainScreenTab.Bookshelf
        }
    }

    override fun onTabSelected(navController: NavController, tab: MainScreenTab) {
        when (tab) {
            MainScreenTab.Bookshelf -> bookshelfGraphRoute
            MainScreenTab.Favorite -> favoriteGraphRoute
            MainScreenTab.Readlater -> readlaterGraphRoute
            MainScreenTab.Library -> libraryGraphRoute
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
