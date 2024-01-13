package com.sorrowblue.comicviewer.app

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.defaults.NestedNavGraphDefaultAnimations
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DependenciesContainerBuilder
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.rememberNavHostEngine
import com.sorrowblue.comicviewer.bookshelf.navigation.BookshelfNavGraph
import com.sorrowblue.comicviewer.bookshelf.navigation.BookshelfNavGraphNavigator
import com.sorrowblue.comicviewer.bookshelf.navigation.bookshelfNavGraphNavigator
import com.sorrowblue.comicviewer.domain.model.AddOn
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.favorite.navigation.FavoriteNavGraph
import com.sorrowblue.comicviewer.favorite.navigation.FavoriteNavGraphNavigator
import com.sorrowblue.comicviewer.favorite.navigation.favoriteNavGraphNavigator
import com.sorrowblue.comicviewer.feature.authentication.destinations.AuthenticationScreenDestination
import com.sorrowblue.comicviewer.feature.authentication.navigation.AuthenticationNavGraph
import com.sorrowblue.comicviewer.feature.authentication.navigation.AuthenticationNavGraphNavigator
import com.sorrowblue.comicviewer.feature.authentication.navigation.Mode
import com.sorrowblue.comicviewer.feature.authentication.navigation.authenticationNavGraphNavigator
import com.sorrowblue.comicviewer.feature.book.destinations.BookScreenDestination
import com.sorrowblue.comicviewer.feature.book.navigation.BookNavGraph
import com.sorrowblue.comicviewer.feature.book.navigation.BookNavGraphNavigator
import com.sorrowblue.comicviewer.feature.book.navigation.bookNavGraphNavigator
import com.sorrowblue.comicviewer.feature.favorite.add.destinations.FavoriteAddScreenDestination
import com.sorrowblue.comicviewer.feature.library.navigation.LibraryNavGraph
import com.sorrowblue.comicviewer.feature.library.navigation.LibraryNavGraphNavigator
import com.sorrowblue.comicviewer.feature.library.navigation.libraryNavGraphNavigator
import com.sorrowblue.comicviewer.feature.library.serviceloader.AddOnNavGraph
import com.sorrowblue.comicviewer.feature.library.serviceloader.BoxNavGraph
import com.sorrowblue.comicviewer.feature.library.serviceloader.DropBoxNavGraph
import com.sorrowblue.comicviewer.feature.library.serviceloader.GoogleDriveNavGraph
import com.sorrowblue.comicviewer.feature.library.serviceloader.OneDriveNavGraph
import com.sorrowblue.comicviewer.feature.readlater.navigation.ReadLaterNavGraph
import com.sorrowblue.comicviewer.feature.readlater.navigation.ReadLaterNavGraphNavigator
import com.sorrowblue.comicviewer.feature.readlater.navigation.readLaterNavGraphNavigator
import com.sorrowblue.comicviewer.feature.search.destinations.SearchScreenDestination
import com.sorrowblue.comicviewer.feature.search.navigation.SearchNavGraph
import com.sorrowblue.comicviewer.feature.search.navigation.SearchNavGraphNavigator
import com.sorrowblue.comicviewer.feature.search.navigation.searchNavGraphNavigator
import com.sorrowblue.comicviewer.feature.settings.destinations.SettingsScreenDestination
import com.sorrowblue.comicviewer.feature.settings.navigation.SettingsNavGraphNavigator
import com.sorrowblue.comicviewer.feature.settings.navigation.dependencySettingsNavGraph
import com.sorrowblue.comicviewer.feature.tutorial.destinations.TutorialScreenDestination
import com.sorrowblue.comicviewer.feature.tutorial.navigation.TutorialNavGraph
import com.sorrowblue.comicviewer.feature.tutorial.navigation.TutorialNavGraphNavigator
import com.sorrowblue.comicviewer.feature.tutorial.navigation.tutorialNavGraphNavigator
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalDimension
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalWindowSize
import com.sorrowblue.comicviewer.framework.designsystem.theme.compactDimension
import com.sorrowblue.comicviewer.framework.designsystem.theme.expandedDimension
import com.sorrowblue.comicviewer.framework.designsystem.theme.mediumDimension
import com.sorrowblue.comicviewer.framework.ui.AnimatedNavGraphSpec
import com.sorrowblue.comicviewer.framework.ui.CoreNavigator
import com.sorrowblue.comicviewer.framework.ui.LifecycleEffect
import com.sorrowblue.comicviewer.framework.ui.NavTabHandler
import com.sorrowblue.comicviewer.framework.ui.rememberSlideDistance

@OptIn(
    ExperimentalMaterialNavigationApi::class,
    ExperimentalMaterial3WindowSizeClassApi::class,
    ExperimentalMaterial3AdaptiveApi::class
)
@Destination
@Composable
internal fun ComicViewerApp(
    savedStateHandle: SavedStateHandle,
    state: ComicViewerAppState = rememberComicViewerAppState(savedStateHandle),
    windowsSize: WindowSizeClass = calculateWindowSizeClass(LocalContext.current as ComponentActivity),
    activity: ComponentActivity = LocalContext.current as ComponentActivity,
    navTabHandler: NavTabHandler = viewModel(activity),
) {
    val dimension = when (windowsSize.widthSizeClass) {
        WindowWidthSizeClass.Compact -> compactDimension
        WindowWidthSizeClass.Medium -> mediumDimension
        WindowWidthSizeClass.Expanded -> expandedDimension
        else -> compactDimension
    }
    CompositionLocalProvider(
        LocalWindowSize provides windowsSize,
        LocalDimension provides dimension,
    ) {
        ComicTheme {
            val addOnList = state.addOnList
            val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
            val slideDistance = rememberSlideDistance()
            val uiState = state.uiState
            MainScreen(
                uiState = uiState,
                bottomSheetNavigator = state.bottomSheetNavigator,
                navController = state.navController,
                onTabSelected = { navController, tab ->
                    state.graphStateHolder.onTabSelected(
                        navController,
                        tab,
                        navTabHandler
                    )
                },
            ) {
                DestinationsNavHost(
                    navGraph = RootNavGraph,
                    navController = state.navController,
                    engine = rememberNavHostEngine(
                        defaultAnimationsForNestedNavGraph = RootNavGraph.allNestedNavGraphs
                            .associateWith {
                                if (it is AnimatedNavGraphSpec) {
                                    it.animations(slideDistance)
                                } else {
                                    NestedNavGraphDefaultAnimations()
                                }
                            }
                    ),
                    dependenciesContainerBuilder = {
                        mainDependency(
                            addOnList = addOnList,
                            onRestoreComplete = state::completeRestoreHistory,
                            onBack = { ActivityCompat.finishAffinity(activity) },
                            onAuthCompleted = { handleBack ->
                                if (handleBack) {
                                    state.navController.popBackStack()
                                } else {
                                    state.navController.navigate(RootNavGraph.startRoute) {
                                        popUpTo(AuthenticationScreenDestination.route) {
                                            inclusive = true
                                        }
                                    }
                                }
                            },
                            onTutorialExit = state::onCompleteTutorial
                        )
                    }
                )
            }
            @Suppress("KotlinConstantConditions")
            if (BuildConfig.BUILD_TYPE != "release") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(
                            (WindowInsets.statusBars.getTop(LocalDensity.current) / LocalDensity.current.density).dp
                        )
                        .background(ComicTheme.colorScheme.tertiaryContainer.copy(alpha = 0.25f))
                        .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
                        .padding(end = 80.dp)
                ) {
                    Text(
                        text = BuildConfig.BUILD_TYPE,
                        color = ComicTheme.colorScheme.onTertiaryContainer,
                        style = ComicTheme.typography.titleMedium,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    )
                }
            }
        }
    }
    LifecycleEffect(Lifecycle.Event.ON_CREATE, action = state::onCreate)
    LifecycleEffect(Lifecycle.Event.ON_START, action = state::onStart)
}

fun DependenciesContainerBuilder<*>.mainDependency(
    addOnList: SnapshotStateList<AddOn>,
    onRestoreComplete: () -> Unit,
    onBack: () -> Unit,
    onAuthCompleted: (Boolean) -> Unit,
    onTutorialExit: () -> Unit,
) {
    dependency(object : CoreNavigator {
        override fun navigateUp() {
            navController.navigateUp()
        }
    })
    dependency(BookshelfNavGraph) {
        bookshelfNavGraphNavigator(object : BookshelfNavGraphNavigator {
            override fun navigateToBook(book: Book) {
                navController.navigate(
                    BookScreenDestination(book.bookshelfId, book.path, book.name)
                )
            }

            override fun onFavoriteClick(file: File) {
                navController.navigate(FavoriteAddScreenDestination(file.bookshelfId, file.path))
            }

            override fun onSearchClick(bookshelfId: BookshelfId, path: String) {
                navController.navigate(SearchScreenDestination(bookshelfId, path))
            }

            override fun onRestoreComplete() {
                onRestoreComplete()
            }

            override fun onSettingsClick() {
                navController.navigate(SettingsScreenDestination)
            }
        })
    }

    dependency(BookNavGraph) {
        bookNavGraphNavigator(object : BookNavGraphNavigator {
            override fun onSettingsClick() {
                navController.navigate(SettingsScreenDestination)
            }
        })
    }

    dependency(ReadLaterNavGraph) {
        readLaterNavGraphNavigator(object : ReadLaterNavGraphNavigator {
            override fun navigateToBook(book: Book) {
                navController.navigate(
                    BookScreenDestination(book.bookshelfId, book.path, book.name)
                )
            }

            override fun onFavoriteClick(file: File) {
                navController.navigate(FavoriteAddScreenDestination(file.bookshelfId, file.path))
            }

            override fun onSearchClick(bookshelfId: BookshelfId, path: String) {
                navController.navigate(SearchScreenDestination(bookshelfId, path))
            }

            override fun onSettingsClick() {
                navController.navigate(SettingsScreenDestination)
            }
        })
    }

    dependency(SearchNavGraph) {
        searchNavGraphNavigator(object : SearchNavGraphNavigator {
            override fun navigateToBook(book: Book) {
                navController.navigate(
                    BookScreenDestination(book.bookshelfId, book.path, book.name)
                )
            }

            override fun onFavoriteClick(file: File) {
                navController.navigate(FavoriteAddScreenDestination(file.bookshelfId, file.path))
            }

            override fun onSearchClick(bookshelfId: BookshelfId, path: String) {
                navController.navigate(SearchScreenDestination(bookshelfId, path))
            }

            override fun onSettingsClick() {
                navController.navigate(SettingsScreenDestination)
            }
        })
    }

    dependency(FavoriteNavGraph) {
        favoriteNavGraphNavigator(object : FavoriteNavGraphNavigator {
            override fun navigateToBook(book: Book) {
                navController.navigate(
                    BookScreenDestination(book.bookshelfId, book.path, book.name)
                )
            }

            override fun navigateToBook(book: Book, favoriteId: FavoriteId) {
                navController.navigate(
                    BookScreenDestination(book.bookshelfId, book.path, book.name, favoriteId)
                )
            }

            override fun onFavoriteClick(file: File) {
                navController.navigate(FavoriteAddScreenDestination(file.bookshelfId, file.path))
            }

            override fun onSearchClick(bookshelfId: BookshelfId, path: String) {
                navController.navigate(SearchScreenDestination(bookshelfId, path))
            }

            override fun onSettingsClick() {
                navController.navigate(SettingsScreenDestination)
            }
        })
    }

    dependencySettingsNavGraph(object : SettingsNavGraphNavigator {
        override fun onStartTutorialClick() {
            navController.navigate(TutorialScreenDestination)
        }

        override fun onPasswordChange() {
            navController.navigate(
                AuthenticationScreenDestination(
                    mode = Mode.Change,
                    handleBack = false
                )
            )
        }

        override fun navigateToChangeAuth(enabled: Boolean) {
            if (enabled) {
                navController.navigate(AuthenticationScreenDestination(Mode.Register, false))
            } else {
                navController.navigate(AuthenticationScreenDestination(Mode.Erase, false))
            }
        }
    })

    dependency(AuthenticationNavGraph) {
        authenticationNavGraphNavigator(object : AuthenticationNavGraphNavigator {
            override fun onBack() {
                onBack()
            }

            override fun onAuthCompleted(handleBack: Boolean, mode: Mode) {
                when (mode) {
                    Mode.Register, Mode.Change, Mode.Erase -> navController.popBackStack()
                    Mode.Authentication -> onAuthCompleted(handleBack)
                }
            }
        })
    }

    dependency(TutorialNavGraph) {
        tutorialNavGraphNavigator(object : TutorialNavGraphNavigator {
            override fun onComplete() {
                onTutorialExit()
            }

        })
    }

    dependency(LibraryNavGraph) {
        libraryNavGraphNavigator(object : LibraryNavGraphNavigator {
            override fun onSettingsClick() {
                navController.navigate(SettingsScreenDestination)
            }

            override fun navigateToBook(book: Book) {
                navController.navigate(
                    BookScreenDestination(book.bookshelfId, book.path, book.name)
                )
            }

            override fun onFavoriteClick(file: File) {
                navController.navigate(FavoriteAddScreenDestination(file.bookshelfId, file.path))
            }
        })
    }
    addOnList.forEach { it.findNavGraph()?.dependency() }
}

fun AddOn.findNavGraph(): AddOnNavGraph? {
    return when (this) {
        AddOn.Document -> null
        AddOn.GoogleDrive -> GoogleDriveNavGraph()
        AddOn.OneDrive -> OneDriveNavGraph()
        AddOn.Dropbox -> DropBoxNavGraph()
        AddOn.Box -> BoxNavGraph()
    }
}
