package com.sorrowblue.comicviewer.app

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation.NavGraphBuilder
import com.sorrowblue.comicviewer.bookshelf.navigation.bookshelfGraph
import com.sorrowblue.comicviewer.domain.model.AddOn
import com.sorrowblue.comicviewer.favorite.navigation.favoriteGroup
import com.sorrowblue.comicviewer.feature.authentication.navigation.Mode
import com.sorrowblue.comicviewer.feature.authentication.navigation.authenticationScreen
import com.sorrowblue.comicviewer.feature.authentication.navigation.navigateToAuthentication
import com.sorrowblue.comicviewer.feature.book.navigation.bookScreen
import com.sorrowblue.comicviewer.feature.book.navigation.navigateToBook
import com.sorrowblue.comicviewer.feature.favorite.add.navigation.favoriteAddScreen
import com.sorrowblue.comicviewer.feature.favorite.add.navigation.navigateToFavoriteAdd
import com.sorrowblue.comicviewer.feature.library.navigation.libraryGroup
import com.sorrowblue.comicviewer.feature.library.serviceloader.AddOnNavigation
import com.sorrowblue.comicviewer.feature.library.serviceloader.BoxNavigation
import com.sorrowblue.comicviewer.feature.library.serviceloader.DropBoxNavigation
import com.sorrowblue.comicviewer.feature.library.serviceloader.GoogleDriveNavigation
import com.sorrowblue.comicviewer.feature.library.serviceloader.OneDriveNavigation
import com.sorrowblue.comicviewer.feature.readlater.navigation.readlaterGroup
import com.sorrowblue.comicviewer.feature.search.navigation.navigateToSearch
import com.sorrowblue.comicviewer.feature.search.navigation.searchGraph
import com.sorrowblue.comicviewer.feature.settings.navigation.navigateToSettings
import com.sorrowblue.comicviewer.feature.settings.navigation.settingsScreen
import com.sorrowblue.comicviewer.feature.tutorial.navigation.navigateToTutorial
import com.sorrowblue.comicviewer.feature.tutorial.navigation.tutorialScreen
import com.sorrowblue.comicviewer.framework.ui.ComposeValue
import java.util.ServiceLoader

context(ComposeValue)
internal fun NavGraphBuilder.mainGraph(
    restoreComplete: () -> Unit,
    onTutorialExit: () -> Unit,
    onBackClick: () -> Unit,
    onAuthCompleted: (Boolean) -> Unit,
    addOnList: SnapshotStateList<AddOn>,
) {
    authenticationScreen(
        onBack = onBackClick,
        onBackClick = navController::navigateUp,
        onAuthCompleted = { back, mode ->
            when (mode) {
                Mode.Register, Mode.Change, Mode.Erase -> navController.popBackStack()
                Mode.Authentication -> onAuthCompleted(back)
            }
        }
    )
    bookshelfGraph(
        onSettingsClick = navController::navigateToSettings,
        navigateToBook = navController::navigateToBook,
        navigateToSearch = navController::navigateToSearch,
        onFavoriteClick = navController::navigateToFavoriteAdd,
        onRestoreComplete = restoreComplete,
    )
    favoriteGroup(
        navigateToBook = navController::navigateToBook,
        onFavoriteBookClick = navController::navigateToBook,
        onSettingsClick = navController::navigateToSettings,
        onSearchClick = navController::navigateToSearch,
        onFavoriteClick = navController::navigateToFavoriteAdd,
    )
    readlaterGroup(
        navigateToBook = navController::navigateToBook,
        onSettingsClick = navController::navigateToSettings,
        navigateToSearch = navController::navigateToSearch,
        onFavoriteClick = navController::navigateToFavoriteAdd
    )
    libraryGroup(
        navigateToBook = { navController.navigateToBook(it) },
        onSettingsClick = navController::navigateToSettings,
        onFavoriteClick = navController::navigateToFavoriteAdd,
        onAddOnClick = { addOn ->
            addOn.addOn.loadDynamicFeature()?.let {
                with(it) {
                    navController.navigateToAddOnScreen()
                }
            }
        }
    )
    favoriteAddScreen(
        onBackClick = navController::popBackStack,
    )

    searchGraph(
        navigateToBook = navController::navigateToBook,
        navigateToSettings = navController::navigateToSettings,
        navigateToFavoriteAdd = navController::navigateToFavoriteAdd
    )

    tutorialScreen(onComplete = onTutorialExit)

    bookScreen(
        onBackClick = navController::popBackStack,
        onSettingsClick = navController::navigateToSettings,
    )

    settingsScreen(
        onBackClick = navController::navigateUp,
        onChangeAuthEnabled = {
            if (it) {
                navController.navigateToAuthentication(Mode.Register)
            } else {
                navController.navigateToAuthentication(Mode.Erase)
            }
        },
        onPasswordChangeClick = { navController.navigateToAuthentication(Mode.Change) },
        onStartTutorialClick = navController::navigateToTutorial,
    )

    addOnList.forEach {
        with(it.loadDynamicFeature() ?: return@forEach) {
            addOnScreen(navController)
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
