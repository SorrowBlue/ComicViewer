/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.savedstate.compose.serialization.serializers.SnapshotStateListSerializer
import androidx.savedstate.serialization.SavedStateConfiguration
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.feature.bookshelf.navigation.BookshelfFolderNavKey
import com.sorrowblue.comicviewer.feature.bookshelf.navigation.BookshelfNavKey
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.navigation.rememberNavigator
import com.sorrowblue.comicviewer.framework.ui.navigation3.NavigationEntryProvider
import com.sorrowblue.comicviewer.framework.ui.navigation3.ScreenEntryProvider
import com.sorrowblue.comicviewer.framework.ui.navigation3.serializer
import com.sorrowblue.comicviewer.framework.ui.navigation3.subclass
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import kotlinx.serialization.modules.polymorphic
import logcat.logcat

data class RestoreNavigation(
    val bookshelfId: BookshelfId,
    val path: String,
    val restorePath: String?,
    val onRestoreComplete: (() -> Unit)? = null,
)

interface ComicViewerAppState {
    val entryProvider: (NavKey) -> NavEntry<NavKey>
    val navigator: Navigator

    fun onNavigationHistoryRestore()
}

@Composable
fun rememberComicViewerUIState(
    allowNavigationRestored: Boolean = true,
    mainViewModel: MainViewModel = viewModel { MainViewModel() },
    viewModel: ComicViewerAppViewModel =
        assistedMetroViewModel<ComicViewerAppViewModel, ComicViewerAppViewModel.Factory> {
            create(
                allowNavigationRestored,
                {
                    mainViewModel.shouldKeepSplash.value = false
                    mainViewModel.isInitialized.value = true
                },
            )
        },
): ComicViewerAppState {
    val navigator = rememberNavigator(
        startKey = BookshelfNavKey,
        topLevelRoutes = viewModel.navigationKeys.sortedBy { it.order }.toSet(),
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                contextual(SnapshotStateListSerializer(PolymorphicSerializer(NavKey::class)))
                polymorphic(NavKey::class) {
                    viewModel.navKeySubclassMap.forEach {
                        subclass(it.subclass, it.serializer)
                    }
                }
            }
        },
    )
    val coroutineScope = rememberCoroutineScope()
    return remember(allowNavigationRestored) {
        ComicViewerAppStateImpl(
            coroutineScope = coroutineScope,
            navigator = navigator,
            restoreNavigation = viewModel.restoreNavigation,
            navigationHistoryRestore = viewModel::onNavigationHistoryRestore,
            navigationEntryProvider = viewModel.navigationEntryProvider,
            screenEntryProviders = viewModel.screenEntryProviders,
        )
    }
}

private class ComicViewerAppStateImpl(
    coroutineScope: CoroutineScope,
    restoreNavigation: SharedFlow<RestoreNavigation>,
    override val navigator: Navigator,
    private val navigationHistoryRestore: () -> Unit,
    private val screenEntryProviders: Set<ScreenEntryProvider>,
    private val navigationEntryProvider: Set<NavigationEntryProvider>,
) : ComicViewerAppState {

    init {
        restoreNavigation.onEach {
            navigator.navigate(
                BookshelfFolderNavKey(
                    bookshelfId = it.bookshelfId,
                    path = it.path,
                    restorePath = it.restorePath,
                    onRestoreComplete = it.onRestoreComplete,
                ),
            )
        }.launchIn(coroutineScope)
    }

    override val entryProvider: (NavKey) -> NavEntry<NavKey> = entryProvider {
        screenEntryProviders.forEach { entryProvider -> entryProvider(navigator) }
        navigationEntryProvider.forEach { provider ->
            logcat { "navigationEntryProvider: $provider" }
            provider(navigator = navigator)
        }
    }

    override fun onNavigationHistoryRestore() {
        logcat { "onNavigationHistoryRestore" }
        navigationHistoryRestore()
    }
}
