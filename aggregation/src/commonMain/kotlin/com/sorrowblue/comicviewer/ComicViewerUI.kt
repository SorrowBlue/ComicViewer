package com.sorrowblue.comicviewer

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.background
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigation.BackNavigationBehavior
import androidx.compose.material3.adaptive.navigation3.kmp.rememberListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.kmp.rememberSupportingPaneSceneStrategy
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.feature.authentication.ScreenType
import com.sorrowblue.comicviewer.feature.authentication.navigation.AuthenticationKey
import com.sorrowblue.comicviewer.feature.authentication.navigation.authenticationEntryGroup
import com.sorrowblue.comicviewer.feature.book.navigation.BookKey
import com.sorrowblue.comicviewer.feature.book.navigation.bookEntryGroup
import com.sorrowblue.comicviewer.feature.bookshelf.navigation.BookshelfKey
import com.sorrowblue.comicviewer.feature.bookshelf.navigation.bookshelfEntryGroup
import com.sorrowblue.comicviewer.feature.collection.navigation.CollectionKey
import com.sorrowblue.comicviewer.feature.collection.navigation.collectionEntryGroup
import com.sorrowblue.comicviewer.feature.history.navigation.HistoryKey
import com.sorrowblue.comicviewer.feature.history.navigation.historyEntryGroup
import com.sorrowblue.comicviewer.feature.readlater.navigation.ReadLaterKey
import com.sorrowblue.comicviewer.feature.readlater.navigation.readLaterEntryGroup
import com.sorrowblue.comicviewer.feature.search.navigation.SearchKey
import com.sorrowblue.comicviewer.feature.search.navigation.searchEntryGroup
import com.sorrowblue.comicviewer.feature.settings.navigation.SettingsKey
import com.sorrowblue.comicviewer.feature.settings.navigation.settingsEntryGroup
import com.sorrowblue.comicviewer.feature.tutorial.navigation.TutorialKey
import com.sorrowblue.comicviewer.feature.tutorial.navigation.tutorialEntryGroup
import com.sorrowblue.comicviewer.folder.navigation.sortTypeSelectEntry
import com.sorrowblue.comicviewer.framework.common.LocalPlatformContext
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import com.sorrowblue.comicviewer.framework.common.platformGraph
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.AdaptiveNavigationSuiteState
import com.sorrowblue.comicviewer.framework.ui.LocalAdaptiveNavigationSuiteState
import com.sorrowblue.comicviewer.framework.ui.LocalAppState
import com.sorrowblue.comicviewer.framework.ui.LocalGlobalSnackbarState
import com.sorrowblue.comicviewer.framework.ui.LocalSharedTransitionScope
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.LocalCoroutineScope
import com.sorrowblue.comicviewer.framework.ui.navigation.AppNavigationState
import com.sorrowblue.comicviewer.framework.ui.navigation.NavigationKey
import com.sorrowblue.comicviewer.framework.ui.rememberAppState
import com.sorrowblue.comicviewer.framework.ui.rememberGlobalSnackbarState
import io.github.irgaly.navigation3.resultstate.rememberNavigationResultNavEntryDecorator
import logcat.logcat

@Composable
fun rememberAdaptiveNavigationSuiteState(
    appNavigationState: AppNavigationState,
): AdaptiveNavigationSuiteState {
    val navigationSuiteType = NavigationSuiteScaffoldDefaults.navigationSuiteType(
        currentWindowAdaptiveInfo()
    )
    val state = remember {
        AdaptiveNavigationSuiteStateImpl(
            appNavigationState = appNavigationState,
            navigationSuiteType = navigationSuiteType
        )
    }
    LaunchedEffect(appNavigationState.currentBackStack.lastOrNull()) {
        when (val screenKey = appNavigationState.currentBackStack.lastOrNull()) {
            is BookshelfKey,
            is CollectionKey,
            is ReadLaterKey,
            is HistoryKey,
            -> {
                state.currentNavItem = screenKey
            }
        }
    }
    return state
}

private class AdaptiveNavigationSuiteStateImpl(
    private val appNavigationState: AppNavigationState,
    override var navigationSuiteType: NavigationSuiteType,
) : AdaptiveNavigationSuiteState {

    override val navItems =
        listOf(BookshelfKey.List, CollectionKey.List, ReadLaterKey.List, HistoryKey.List)

    override var currentNavItem: NavigationKey by mutableStateOf(BookshelfKey.List)

    override fun onNavItemClick(navItem: NavigationKey) {
        currentNavItem = navItem
        when (navItem) {
            is BookshelfKey -> {
                appNavigationState.currentBackStack.add(BookshelfKey.List)
            }

            is CollectionKey -> {
                appNavigationState.currentBackStack.add(CollectionKey.List)
            }

            is HistoryKey -> {
                appNavigationState.currentBackStack.add(HistoryKey.List)
            }

            is ReadLaterKey -> {
                appNavigationState.currentBackStack.add(ReadLaterKey.List)
            }
        }
    }
}

@Composable
context(context: PlatformContext)
fun ComicViewerUI(bookData: String?) {
    CompositionLocalProvider(LocalPlatformContext provides context) {
        ComicTheme {
            SharedTransitionLayout(modifier = Modifier.background(ComicTheme.colorScheme.background)) {
                val appNavigationState = rememberAppNavigationState(bookData)
                val state = rememberAdaptiveNavigationSuiteState(appNavigationState)
                val scope = rememberCoroutineScope()
                val appState = rememberAppState(this)
                val globalSnackbarHostState = rememberGlobalSnackbarState()
                CompositionLocalProvider(
                    LocalSharedTransitionScope provides this,
                    LocalAdaptiveNavigationSuiteState provides state,
                    LocalAppState provides appState,
                    LocalCoroutineScope provides scope,
                    LocalGlobalSnackbarState provides globalSnackbarHostState
                ) {
                    Scaffold(
                        snackbarHost = { SnackbarHost(globalSnackbarHostState.snackbarHostState) }
                    ) {
                        val platformGraph = context.platformGraph
                        val supportingPaneSceneStrategy =
                            rememberSupportingPaneSceneStrategy<NavKey>(
                                backNavigationBehavior = BackNavigationBehavior.PopUntilContentChange
                            )
                        val listDetailSceneStrategy = rememberListDetailSceneStrategy<NavKey>()
                        val entryProvider = entryProvider {
                            with(platformGraph) {
                                with(appNavigationState) {
                                    val onSettingsClick = {
                                        appNavigationState.addToBackStack(SettingsKey)
                                    }
                                    bookshelfEntryGroup(
                                        onSettingsClick = onSettingsClick,
                                        onSearchClick = { id, path ->
                                            appNavigationState.addToBackStack(
                                                SearchKey.List(
                                                    id,
                                                    path
                                                )
                                            )
                                        },
                                        onBookClick = { book ->
                                            appNavigationState.addToBackStack(
                                                BookKey(
                                                    bookshelfId = book.bookshelfId,
                                                    path = book.path,
                                                    name = book.name
                                                )
                                            )
                                        },
                                        onRestored = {
                                            logcat { "Bookshelf restored" }
                                        },
                                        onCollectionClick = { file ->
                                            /* TODO */
                                        }
                                    )
                                    historyEntryGroup(
                                        onSettingsClick = onSettingsClick,
                                        onBookClick = { book ->
                                            appNavigationState.addToBackStack(
                                                BookKey(
                                                    bookshelfId = book.bookshelfId,
                                                    path = book.path,
                                                    name = book.name
                                                )
                                            )
                                        },
                                        onCollectionClick = {}
                                    )
                                    collectionEntryGroup(
                                        onSettingsClick = onSettingsClick,
                                        onSearchClick = { id, path ->
                                            appNavigationState.addToBackStack(
                                                SearchKey.List(
                                                    id,
                                                    path
                                                )
                                            )
                                        },
                                        onCollectionBookClick = { book, collectionId ->
                                            appNavigationState.addToBackStack(
                                                BookKey(
                                                    bookshelfId = book.bookshelfId,
                                                    path = book.path,
                                                    name = book.name,
                                                    collectionId = collectionId
                                                )
                                            )
                                        },
                                        onBookClick = { book ->
                                            appNavigationState.addToBackStack(
                                                BookKey(
                                                    bookshelfId = book.bookshelfId,
                                                    path = book.path,
                                                    name = book.name
                                                )
                                            )
                                        }
                                    )
                                    readLaterEntryGroup(
                                        onSettingsClick = onSettingsClick,
                                        onSearchClick = { id, path ->
                                            appNavigationState.addToBackStack(
                                                SearchKey.List(
                                                    id,
                                                    path
                                                )
                                            )
                                        },
                                        onBookClick = { book ->
                                            appNavigationState.addToBackStack(
                                                BookKey(
                                                    bookshelfId = book.bookshelfId,
                                                    path = book.path,
                                                    name = book.name
                                                )
                                            )
                                        },
                                        onCollectionClick = { book ->
                                            appNavigationState.addToBackStack(
                                                BookKey(
                                                    bookshelfId = book.bookshelfId,
                                                    path = book.path,
                                                    name = book.name
                                                )
                                            )
                                        }
                                    )

                                    searchEntryGroup(
                                        onSettingsClick = onSettingsClick,
                                        onSearchClick = { id, path ->
                                            appNavigationState.addToBackStack(
                                                SearchKey.List(id, path)
                                            )
                                        },
                                        onBookClick = { book ->
                                            appNavigationState.addToBackStack(
                                                BookKey(
                                                    bookshelfId = book.bookshelfId,
                                                    path = book.path,
                                                    name = book.name
                                                )
                                            )
                                        },
                                        onCollectionClick = { book ->
                                            appNavigationState.addToBackStack(
                                                BookKey(
                                                    bookshelfId = book.bookshelfId,
                                                    path = book.path,
                                                    name = book.name,
                                                )
                                            )
                                        },
                                        onSmartCollectionClick = { id, searchCondition ->
                                            appNavigationState.addToBackStack(
                                                CollectionKey.CreateSmart(id, searchCondition)
                                            )
                                        }
                                    )
                                    settingsEntryGroup(
                                        onChangeAuthEnable = {
                                            if (it) {
                                                appNavigationState.addToBackStack(
                                                    AuthenticationKey(
                                                        ScreenType.Register
                                                    )
                                                )
                                            } else {
                                                appNavigationState.addToBackStack(
                                                    AuthenticationKey(
                                                        ScreenType.Erase
                                                    )
                                                )
                                            }
                                        },
                                        onPasswordChangeClick = {
                                            appNavigationState.addToBackStack(
                                                AuthenticationKey(
                                                    ScreenType.Change
                                                )
                                            )
                                        },
                                        onTutorialClick = {
                                            appNavigationState.addToBackStack(TutorialKey)
                                        }
                                    )
                                    tutorialEntryGroup()
                                    authenticationEntryGroup()

                                    sortTypeSelectEntry(
                                        onBackClick = {
                                            appNavigationState.onBackPressed()
                                        }
                                    )

                                    bookEntryGroup(onSettingsClick = onSettingsClick)
                                }
                            }
                        }
                        NavDisplay(
                            entryDecorators = listOf(
                                rememberNavigationResultNavEntryDecorator(
                                    backStack = appNavigationState.currentBackStack,
                                    entryProvider = entryProvider,
                                ),
                                rememberSaveableStateHolderNavEntryDecorator(),
                                rememberViewModelStoreNavEntryDecorator()
                            ),
                            sceneStrategy = supportingPaneSceneStrategy
                                .then(listDetailSceneStrategy)
                                .then(remember { DialogSceneStrategy() }),
                            backStack = appNavigationState.currentBackStack,
                            entryProvider = entryProvider
                        )
                    }
                }
            }
        }
    }
}
