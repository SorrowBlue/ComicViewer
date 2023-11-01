package com.sorrowblue.comicviewer.app

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.google.android.play.core.review.ReviewManagerFactory
import com.mikepenz.aboutlibraries.LibsBuilder
import com.sorrowblue.comicviewer.bookshelf.navigation.bookshelfGraph
import com.sorrowblue.comicviewer.domain.model.AddOn
import com.sorrowblue.comicviewer.favorite.navigation.favoriteGroup
import com.sorrowblue.comicviewer.feature.authentication.navigation.Mode
import com.sorrowblue.comicviewer.feature.authentication.navigation.authenticationScreen
import com.sorrowblue.comicviewer.feature.authentication.navigation.navigateToAuthentication
import com.sorrowblue.comicviewer.feature.book.navigation.bookGraph
import com.sorrowblue.comicviewer.feature.book.navigation.navigateToBook
import com.sorrowblue.comicviewer.feature.favorite.create.navigation.favoriteCreateScreen
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
import com.sorrowblue.comicviewer.feature.settings.navigation.settingsNavGraph
import com.sorrowblue.comicviewer.feature.tutorial.navigation.navigateToTutorial
import com.sorrowblue.comicviewer.feature.tutorial.navigation.tutorialScreen
import java.util.ServiceLoader
import kotlinx.collections.immutable.PersistentList
import logcat.asLog
import logcat.logcat

internal fun NavGraphBuilder.mainGraph(
    isMobile: Boolean,
    context: Context,
    navController: NavHostController,
    extraNavController: ExtraNavController,
    contentPadding: PaddingValues,
    restoreComplete: () -> Unit,
    onTutorialExit: () -> Unit,
    onBackClick: () -> Unit,
    onAuthCompleted: (Boolean) -> Unit,
    addOnList: PersistentList<AddOn>,
) {
    authenticationScreen(
        onBack = onBackClick,
        onAuthCompleted = { back, mode ->
            when (mode) {
                Mode.Register -> {
                    navController.popBackStack()
                }

                Mode.Change -> {
                    navController.popBackStack()
                }

                Mode.Erase -> {
                    navController.popBackStack()
                }

                Mode.Authentication -> onAuthCompleted(back)
            }
        }
    )
    bookshelfGraph(
        isMobile = isMobile,
        contentPadding = contentPadding,
        navController = navController,
        onSettingsClick = navController::navigateToSettings,
        navigateToBook = navController::navigateToBook,
        navigateToSearch = navController::navigateToSearch,
        onRestoreComplete = restoreComplete,
    )
    favoriteGroup(
        contentPadding = contentPadding,
        navController = navController,
        onBookClick = navController::navigateToBook,
        onClickLongFile = { /*TODO*/ },
        onSettingsClick = navController::navigateToSettings,
        navigateToSearch = navController::navigateToSearch,
    )
    favoriteCreateScreen(onDismissRequest = navController::popBackStack)
    readlaterGroup(
        contentPadding = contentPadding,
        navController = navController,
        onBookClick = navController::navigateToBook,
        onFileLongClick = { /*TODO*/ },
        onSettingsClick = navController::navigateToSettings,
        navigateToSearch = navController::navigateToSearch,
    )
    libraryGroup(
        contentPadding = contentPadding,
        navController = navController,
        onBookClick = { id, path, pos ->
            navController.navigateToBook(id, path, position = pos)
        },
        onFileLongClick = {
            /*TODO*/
        },
        onSettingsClick = navController::navigateToSettings,
        navigateToSearch = navController::navigateToSearch,
        onAddOnClick = { addOn ->
            addOn.addOn.loadDynamicFeature()?.let {
                with(it) {
                    navController.navigateToAddOnScreen()
                }
            }
        },
    )

    searchGraph(
        contentPadding = contentPadding,
        navController = navController,
        onClickLongFile = { /*TODO*/ },
        onBookClick = navController::navigateToBook,
        onSettingsClick = navController::navigateToSettings
    )

    tutorialScreen(onComplete = onTutorialExit)

    bookGraph(navController = navController, onBackClick = navController::popBackStack)

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
                                extraNavController.launchUrl("http://play.google.com/store/apps/details?id=${context.packageName}")
                            }
                        }
                } else {
                    logcat { task.exception?.asLog().toString() }
                    extraNavController.launchUrl("http://play.google.com/store/apps/details?id=${context.packageName}")
                }
            }
        },
        onStartTutorialClick = navController::navigateToTutorial,
        onPasswordChangeClick = { navController.navigateToAuthentication(Mode.Change) },
        onChangeAuthEnabled = {
            if (it) {
                navController.navigateToAuthentication(Mode.Register)
            } else {
                navController.navigateToAuthentication(Mode.Erase)
            }
        }
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