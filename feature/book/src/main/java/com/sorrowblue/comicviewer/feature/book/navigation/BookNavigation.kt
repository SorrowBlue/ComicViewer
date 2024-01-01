package com.sorrowblue.comicviewer.feature.book.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.utils.composable
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.feature.book.BookScreen
import com.sorrowblue.comicviewer.feature.book.destinations.BookScreenDestination
import com.sorrowblue.comicviewer.framework.ui.ComposeTransition
import com.sorrowblue.comicviewer.framework.ui.ComposeValue
import com.sorrowblue.comicviewer.framework.ui.animatedNavigation

context(ComposeValue)
fun NavGraphBuilder.bookScreen(
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    animatedNavigation(
        BookScreenDestination.route,
        route = "books",
        transitions = listOf(
            ComposeTransition(
                BookScreenDestination.route,
                null,
                ComposeTransition.Type.ContainerTransform
            )
        )
    ) {
        composable(BookScreenDestination) {
            BookScreen(
                args = navArgs,
                onBackClick = onBackClick,
                onSettingsClick = onSettingsClick,
                onNextBookClick = { book, favoriteId ->
                    navController.navigateToBook(
                        book = book,
                        favoriteId = favoriteId
                    ) {
                        popUpTo(BookScreenDestination.route) {
                            inclusive = true
                        }
                    }
                },
                contentPadding = contentPadding
            )
        }
    }
}

fun NavController.navigateToBook(
    book: Book,
    favoriteId: FavoriteId = FavoriteId.Default,
    navOptionsBuilder: NavOptionsBuilder.() -> Unit = {},
) {
    navigate(
        BookScreenDestination(
            bookshelfId = book.bookshelfId,
            path = book.path,
            name = book.name,
            favoriteId = favoriteId
        ),
        navOptionsBuilder = navOptionsBuilder
    )
}

class BookArgs(
    val bookshelfId: BookshelfId,
    val path: String,
    val name: String,
    val favoriteId: FavoriteId = FavoriteId.Default,
)
