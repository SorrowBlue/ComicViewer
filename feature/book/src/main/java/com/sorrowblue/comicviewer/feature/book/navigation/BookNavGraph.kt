package com.sorrowblue.comicviewer.feature.book.navigation

import androidx.navigation.NavController
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.scope.DestinationScopeWithNoDependencies
import com.ramcosta.composedestinations.spec.DestinationSpec
import com.ramcosta.composedestinations.spec.Route
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.feature.book.BookScreenNavigator
import com.sorrowblue.comicviewer.feature.book.destinations.BookScreenDestination
import com.sorrowblue.comicviewer.framework.ui.AnimatedNavGraphSpec
import com.sorrowblue.comicviewer.framework.ui.TransitionsConfigure

object BookNavGraph : AnimatedNavGraphSpec {
    override val route = "book_graph"
    override val startRoute: Route = BookScreenDestination
    override val destinationsByRoute: Map<String, DestinationSpec<*>> = listOf(
        BookScreenDestination,
    ).associateBy { it.route }
    override val transitions = listOf(
        TransitionsConfigure(
            BookScreenDestination.route,
            null,
            TransitionsConfigure.Type.ContainerTransform
        )
    )
}

fun DestinationScopeWithNoDependencies<*>.bookNavGraphNavigator(navigator: BookNavGraphNavigator) =
    BookNavGraphNavigatorImpl(navigator, navController)

interface BookNavGraphNavigator {
    fun onSettingsClick()
}

class BookNavGraphNavigatorImpl internal constructor(
    navigator: BookNavGraphNavigator,
    private val navController: NavController,
) : BookScreenNavigator,
    BookNavGraphNavigator by navigator {

    override fun onNextBookClick(book: Book, favoriteId: FavoriteId) {
        navController.navigate(
            BookScreenDestination(
                bookshelfId = book.bookshelfId,
                path = book.path,
                name = book.name,
                favoriteId = favoriteId
            )
        ) {
            popUpTo(BookScreenDestination.route) {
                inclusive = true
            }
        }
    }

    override fun navigateUp() {
        navController.navigateUp()
    }
}
