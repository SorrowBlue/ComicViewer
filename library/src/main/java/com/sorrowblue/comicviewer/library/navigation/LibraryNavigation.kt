package com.sorrowblue.comicviewer.library.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.feature.history.navigation.historyGroup
import com.sorrowblue.comicviewer.feature.history.navigation.navigateToHistoryGroup
import com.sorrowblue.comicviewer.library.CloudStorage
import com.sorrowblue.comicviewer.library.LibraryRoute
import com.sorrowblue.comicviewer.library.LocalFeature
import com.sorrowblue.comicviewer.library.serviceloader.BoxNavigation
import com.sorrowblue.comicviewer.library.serviceloader.DropBoxNavigation
import com.sorrowblue.comicviewer.library.serviceloader.GoogleDriveNavigation
import com.sorrowblue.comicviewer.library.serviceloader.OneDriveNavigation
import java.util.ServiceLoader

const val LibraryRoute = "library"

private fun NavGraphBuilder.libraryScreen(
    contentPadding: PaddingValues,
    onFeatureClick: (LocalFeature) -> Unit,
    onCloudClick: (CloudStorage) -> Unit,
) {
    composable(LibraryRoute) {
        LibraryRoute(
            contentPadding = contentPadding,
            onFeatureClick = onFeatureClick,
            onCloudClick = onCloudClick
        )
    }
}

const val LibraryGroupRoute = "${LibraryRoute}_group"

fun NavGraphBuilder.libraryGroup(
    contentPadding: PaddingValues,
    navController: NavController,
    onBookClick: (BookshelfId, String) -> Unit,
    onSettingsClick: () -> Unit,
    onAddFavoriteClick: (BookshelfId, String) -> Unit,
    navigateToSearch: (BookshelfId, String) -> Unit
) {
    navigation(route = LibraryGroupRoute, startDestination = LibraryRoute) {
        val dropBoxNavigation = ServiceLoader.load(
            DropBoxNavigation.Provider::class.java,
            DropBoxNavigation.Provider::class.java.classLoader
        ).iterator().next().get()
        val googleDriveNavigation = ServiceLoader.load(
            GoogleDriveNavigation.Provider::class.java,
            GoogleDriveNavigation.Provider::class.java.classLoader
        ).iterator().next().get()
        val boxNavigation = ServiceLoader.load(
            BoxNavigation.Provider::class.java,
            BoxNavigation.Provider::class.java.classLoader
        ).iterator().next().get()
        val oneDriveNavigation = ServiceLoader.load(
            OneDriveNavigation.Provider::class.java,
            OneDriveNavigation.Provider::class.java.classLoader
        ).iterator().next().get()
        libraryScreen(
            contentPadding = contentPadding,
            onFeatureClick = {
                when (it) {
                    LocalFeature.HISTORY -> navController.navigateToHistoryGroup()
                    LocalFeature.DOWNLOADED -> Unit /*TODO()*/
                }
            },
            onCloudClick = {
                when (it) {
                    is CloudStorage.Box -> with(boxNavigation) { navController.navigateToBox() }
                    is CloudStorage.Dropbox -> with(dropBoxNavigation) { navController.navigateToDropBox() }
                    is CloudStorage.GoogleDrive -> with(googleDriveNavigation) { navController.navigateToGoogleDrive() }
                    is CloudStorage.OneDrive -> with(oneDriveNavigation) { navController.navigateToOneDrive() }
                }
            }
        )

        historyGroup(
            contentPadding = contentPadding,
            navController = navController,
            onBookClick = onBookClick,
            onSettingsClick = onSettingsClick,
            onAddFavoriteClick = onAddFavoriteClick,
            navigateToSearch = navigateToSearch
        )

        with(googleDriveNavigation) { googleDriveScreen(navController) }

        with(dropBoxNavigation) { dropBoxScreen(navController) }
        with(boxNavigation) { boxScreen(navController) }
        with(oneDriveNavigation) { oneDriveScreen(navController) }
    }
}
