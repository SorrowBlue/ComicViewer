package com.sorrowblue.comicviewer.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.ramcosta.composedestinations.navigation.DependenciesContainerBuilder
import com.ramcosta.composedestinations.navigation.dependency
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.favorite.navigation.FavoriteGraphDependencies
import com.sorrowblue.comicviewer.feature.bookshelf.navigation.BookshelfGraphDependencies
import com.sorrowblue.comicviewer.feature.favorite.add.FavoriteAddScreenNavigator
import com.sorrowblue.comicviewer.feature.favorite.add.destinations.FavoriteAddDialogScreenDestination
import com.sorrowblue.comicviewer.feature.favorite.create.destinations.FavoriteCreateDialogScreenDestination
import com.sorrowblue.comicviewer.feature.library.navigation.LibraryGraphDependencies
import com.sorrowblue.comicviewer.feature.search.SearchArgs
import com.sorrowblue.comicviewer.feature.search.navgraphs.SearchNavGraph
import com.sorrowblue.comicviewer.feature.search.navigation.SearchGraphDependencies
import com.sorrowblue.comicviewer.feature.settings.navgraphs.SettingsNavGraph
import com.sorrowblue.comicviewer.feature.settings.navigation.SettingsGraphDependencies
import com.sorrowblue.comicviewer.feature.tutorial.navgraphs.TutorialNavGraph
import com.sorrowblue.comicviewer.feature.tutorial.navigation.TutorialGraphDependencies

/**
 * Main dependencies
 *
 * @param onRestoreComplete
 * @receiver
 */
@Composable
internal fun DependenciesContainerBuilder<*>.MainDependencies(
    onRestoreComplete: () -> Unit,
) {
    val onSettingsClick =
        remember(destinationsNavigator) { { destinationsNavigator.navigate(SettingsNavGraph) } }
    val onBookClick =
        { book: com.sorrowblue.comicviewer.domain.model.file.Book, favoriteId: FavoriteId? ->
//        destinationsNavigator.navigate(
//            BookNavGraph(
//                com.sorrowblue.comicviewer.feature.book.Book(
//                    book.bookshelfId,
//                    book.path,
//                    book.name,
//                    favoriteId ?: FavoriteId()
//                )
//            )
//        )
        }
    val onFavoriteClick = { bookshelfId: BookshelfId, path: String ->
        destinationsNavigator.navigate(FavoriteAddDialogScreenDestination(bookshelfId, path))
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
                FavoriteCreateDialogScreenDestination(bookshelfId, path)
            )
        }
    })

//    BookGraphDependencies(onSettingsClick = onSettingsClick)

    BookshelfGraphDependencies(
        onBookClick = { onBookClick(it, null) },
        onFavoriteClick = onFavoriteClick,
        onSearchClick = { bookshelfId, path ->
            destinationsNavigator.navigate(SearchNavGraph(SearchArgs(bookshelfId, path)))
        },
        onRestoreComplete = onRestoreComplete,
        onSettingsClick = onSettingsClick
    )

//    ReadLaterGraphDependencies(
//        onBookClick = { onBookClick(it, null) },
//        onFavoriteClick = onFavoriteClick,
//        onSearchClick = onSearchClick,
//        onSettingsClick = onSettingsClick
//    )

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

    TutorialGraphDependencies(destinationsNavigator::navigateUp)

    LibraryGraphDependencies(
        navigateToBook = onBookClick,
        onSearchClick = onSearchClick,
        onFavoriteClick = onFavoriteClick,
        onSettingsClick = onSettingsClick
    )
}
