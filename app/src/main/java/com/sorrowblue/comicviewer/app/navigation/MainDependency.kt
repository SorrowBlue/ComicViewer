package com.sorrowblue.comicviewer.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation.NavController
import com.ramcosta.composedestinations.navigation.DependenciesContainerBuilder
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.navigation.navigate
import com.sorrowblue.comicviewer.app.findNavGraph
import com.sorrowblue.comicviewer.bookshelf.navigation.dependencyBookshelfNavGraph
import com.sorrowblue.comicviewer.domain.model.AddOn
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.favorite.navigation.dependencyFavoriteNavGraph
import com.sorrowblue.comicviewer.feature.book.destinations.BookScreenDestination
import com.sorrowblue.comicviewer.feature.book.navigation.dependencyBookNavGraph
import com.sorrowblue.comicviewer.feature.favorite.add.destinations.FavoriteAddScreenDestination
import com.sorrowblue.comicviewer.feature.library.navigation.dependencyLibraryNavGraph
import com.sorrowblue.comicviewer.feature.readlater.navigation.dependencyReadLaterNavGraph
import com.sorrowblue.comicviewer.feature.search.destinations.SearchScreenDestination
import com.sorrowblue.comicviewer.feature.search.navigation.dependencySearchNavGraph
import com.sorrowblue.comicviewer.feature.settings.destinations.SettingsScreenDestination
import com.sorrowblue.comicviewer.feature.settings.navigation.dependencySettingsNavGraph
import com.sorrowblue.comicviewer.feature.tutorial.destinations.TutorialScreenDestination
import com.sorrowblue.comicviewer.feature.tutorial.navigation.dependencyTutorialNavGraph
import com.sorrowblue.comicviewer.framework.ui.CoreNavigator

@Composable
fun DependenciesContainerBuilder<*>.mainDependency(
    addOnList: SnapshotStateList<AddOn>,
    onRestoreComplete: () -> Unit,
    onTutorialExit: () -> Unit,
) {
    dependency(object : CoreNavigator {
        override val navController: NavController
            get() = this@mainDependency.navController
        override fun navigateUp() {
            navController.navigateUp()
        }
    })
    dependencyBookNavGraph(
        onSettingsClick = {
            navController.navigate(SettingsScreenDestination)
        }
    )

    dependencyBookshelfNavGraph(
        onBookClick = { book ->
            navController.navigate(BookScreenDestination(book.bookshelfId, book.path, book.name))
        },
        onFavoriteClick = { file: File ->
            navController.navigate(FavoriteAddScreenDestination(file.bookshelfId, file.path))
        },
        onSearchClick = { bookshelfId, path ->
            navController.navigate(SearchScreenDestination(bookshelfId, path))
        },
        onRestoreComplete = onRestoreComplete,
        onSettingsClick = {
            navController.navigate(SettingsScreenDestination)
        }
    )

    dependencyReadLaterNavGraph(
        onBookClick = { book ->
            navController.navigate(BookScreenDestination(book.bookshelfId, book.path, book.name))
        },
        onFavoriteClick = { file: File ->
            navController.navigate(FavoriteAddScreenDestination(file.bookshelfId, file.path))
        },
        onSearchClick = { bookshelfId, path ->
            navController.navigate(SearchScreenDestination(bookshelfId, path))
        },
        onSettingsClick = {
            navController.navigate(SettingsScreenDestination)
        }
    )

    dependencySearchNavGraph(
        onBookClick = { book ->
            navController.navigate(BookScreenDestination(book.bookshelfId, book.path, book.name))
        },
        onFavoriteClick = { file ->
            navController.navigate(FavoriteAddScreenDestination(file.bookshelfId, file.path))
        },
        onSearchClick = { bookshelfId, path ->
            navController.navigate(SearchScreenDestination(bookshelfId, path))
        },
        onSettingsClick = {
            navController.navigate(SettingsScreenDestination)
        }
    )

    dependencyFavoriteNavGraph(
        onBookClick = { book, favoriteId ->
            if (favoriteId == null) {
                BookScreenDestination(book.bookshelfId, book.path, book.name)
            } else {
                BookScreenDestination(book.bookshelfId, book.path, book.name, favoriteId)
            }.let(navController::navigate)
        },
        onFavoriteClick = { file ->
            navController.navigate(FavoriteAddScreenDestination(file.bookshelfId, file.path))
        },
        onSearchClick = { bookshelfId, path ->
            navController.navigate(SearchScreenDestination(bookshelfId, path))
        },
        onSettingsClick = {
            navController.navigate(SettingsScreenDestination)
        }
    )

    dependencySettingsNavGraph(
        onStartTutorialClick = {
            navController.navigate(TutorialScreenDestination)
        }
    )

    dependencyTutorialNavGraph(
        onComplete = onTutorialExit
    )

    dependencyLibraryNavGraph(
        onSettingsClick = {
            navController.navigate(SettingsScreenDestination)
        },
        navigateToBook = { book ->
            navController.navigate(BookScreenDestination(book.bookshelfId, book.path, book.name))
        },
        onFavoriteClick = { file ->
            navController.navigate(FavoriteAddScreenDestination(file.bookshelfId, file.path))
        }
    )

    addOnList.forEach { it.findNavGraph()?.dependency() }
}
