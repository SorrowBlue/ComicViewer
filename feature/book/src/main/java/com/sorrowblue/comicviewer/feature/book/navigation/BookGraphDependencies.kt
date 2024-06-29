package com.sorrowblue.comicviewer.feature.book.navigation

import androidx.compose.runtime.Composable
import com.ramcosta.composedestinations.navigation.DependenciesContainerBuilder
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.navigation.navGraph
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.feature.book.BookScreenNavigator
import com.sorrowblue.comicviewer.feature.book.NavGraphs
import com.sorrowblue.comicviewer.feature.book.destinations.BookMenuScreenDestination
import com.sorrowblue.comicviewer.feature.book.destinations.BookScreenDestination

@Composable
fun DependenciesContainerBuilder<*>.BookGraphDependencies(
    onSettingsClick: () -> Unit,
) {
    navGraph(NavGraphs.book) {
        dependency(object : BookScreenNavigator {
            override fun onSettingsClick() = onSettingsClick()

            override fun onNextBookClick(book: Book, favoriteId: FavoriteId) {
                destinationsNavigator.navigate(
                    BookScreenDestination(
                        bookshelfId = book.bookshelfId,
                        path = book.path,
                        name = book.name,
                        favoriteId = favoriteId
                    )
                ) {
                    popUpTo(BookScreenDestination) {
                        inclusive = true
                    }
                }
            }

            override fun navigateUp() {
                destinationsNavigator.navigateUp()
            }

            override fun onContainerLongClick() {
                destinationsNavigator.navigate(BookMenuScreenDestination)
            }
        })
    }
}
