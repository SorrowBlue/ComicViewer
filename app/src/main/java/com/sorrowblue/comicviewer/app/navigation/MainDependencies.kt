package com.sorrowblue.comicviewer.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.ramcosta.composedestinations.navigation.DependenciesContainerBuilder
import com.ramcosta.composedestinations.navigation.dependency
import com.sorrowblue.comicviewer.bookshelf.navigation.BookshelfGraphDependencies
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.favorite.navigation.FavoriteGraphDependencies
import com.sorrowblue.comicviewer.feature.book.BookArgs
import com.sorrowblue.comicviewer.feature.book.navgraphs.BookNavGraph
import com.sorrowblue.comicviewer.feature.book.navigation.BookGraphDependencies
import com.sorrowblue.comicviewer.feature.favorite.add.FavoriteAddScreenNavigator
import com.sorrowblue.comicviewer.feature.favorite.add.destinations.FavoriteAddDialogScreenDestination
import com.sorrowblue.comicviewer.feature.favorite.create.FavoriteBooksToAdd
import com.sorrowblue.comicviewer.feature.favorite.create.FavoriteCreateDialogScreenArgs
import com.sorrowblue.comicviewer.feature.favorite.create.destinations.FavoriteCreateDialogScreenDestination
import com.sorrowblue.comicviewer.feature.library.navigation.LibraryGraphDependencies
import com.sorrowblue.comicviewer.feature.readlater.navigation.ReadLaterGraphDependencies
import com.sorrowblue.comicviewer.feature.search.SearchArgs
import com.sorrowblue.comicviewer.feature.search.navgraphs.SearchNavGraph
import com.sorrowblue.comicviewer.feature.search.navigation.SearchGraphDependencies
import com.sorrowblue.comicviewer.feature.settings.navgraphs.SettingsNavGraph
import com.sorrowblue.comicviewer.feature.settings.navigation.SettingsGraphDependencies
import com.sorrowblue.comicviewer.feature.tutorial.navgraphs.TutorialNavGraph

@Composable
internal fun DependenciesContainerBuilder<*>.MainDependencies(
    onRestoreComplete: () -> Unit,
) {
    val onSettingsClick =
        remember(destinationsNavigator) { { destinationsNavigator.navigate(SettingsNavGraph) } }
    val onBookClick = remember(destinationsNavigator) {
        {
                book: Book, favoriteId: FavoriteId? ->
            destinationsNavigator.navigate(
                BookNavGraph(
                    BookArgs(
                        book.bookshelfId,
                        book.path,
                        book.name,
                        favoriteId ?: FavoriteId()
                    )
                )
            )
        }
    }
    val onFavoriteClick = remember(destinationsNavigator) {
        {
                file: File ->
            destinationsNavigator.navigate(
                FavoriteAddDialogScreenDestination(file.bookshelfId, file.path)
            )
        }
    }

    val onSearchClick = { bookshelfId: BookshelfId, path: String ->
        destinationsNavigator.navigate(SearchNavGraph(SearchArgs(bookshelfId, path)))
    }

    dependency(object : FavoriteAddScreenNavigator {
        override fun navigateUp() {
            destinationsNavigator.navigateUp()
        }

        override fun navigateToCreateFavorite(bookshelfId: BookshelfId, path: String) {
            destinationsNavigator.navigateUp()
            destinationsNavigator.navigate(
                FavoriteCreateDialogScreenDestination(
                    FavoriteCreateDialogScreenArgs(FavoriteBooksToAdd(bookshelfId, path))
                )
            )
        }
    })

    BookGraphDependencies(onSettingsClick = onSettingsClick)

    BookshelfGraphDependencies(
        onBookClick = { onBookClick(it, null) },
        onFavoriteClick = onFavoriteClick,
        onSearchClick = { bookshelfId, path ->
            destinationsNavigator.navigate(SearchNavGraph(SearchArgs(bookshelfId, path)))
        },
        onRestoreComplete = onRestoreComplete,
        onSettingsClick = onSettingsClick
    )

    ReadLaterGraphDependencies(
        onBookClick = { onBookClick(it, null) },
        onFavoriteClick = onFavoriteClick,
        onSearchClick = onSearchClick,
        onSettingsClick = onSettingsClick
    )

    SearchGraphDependencies(
        onBookClick = { onBookClick(it, null) },
        onFavoriteClick = onFavoriteClick,
        onSearchClick = onSearchClick,
        onSettingsClick = onSettingsClick
    )

    FavoriteGraphDependencies(
        onBookClick = onBookClick,
        onFavoriteClick = onFavoriteClick,
        onSearchClick = onSearchClick,
        onSettingsClick = onSettingsClick,
        onNewFavoriteClick = {
            destinationsNavigator.navigate(FavoriteCreateDialogScreenDestination())
        }
    )

    SettingsGraphDependencies(
        onStartTutorialClick = {
            destinationsNavigator.navigate(TutorialNavGraph)
        }
    )

    LibraryGraphDependencies(
        navigateToBook = { onBookClick(it, null) },
        onFavoriteClick = onFavoriteClick,
        onSettingsClick = onSettingsClick
    )
}
