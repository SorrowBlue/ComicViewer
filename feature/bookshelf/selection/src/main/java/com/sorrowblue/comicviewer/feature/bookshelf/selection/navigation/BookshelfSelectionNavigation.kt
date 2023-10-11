package com.sorrowblue.comicviewer.feature.bookshelf.selection.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.feature.bookshelf.selection.BookshelfSelectionScreen

const val BookshelfSelectionRoute = "bookshelf/selection"

fun NavController.navigateToBookshelfSelection(navOptions: NavOptions? = null) {
    navigate(BookshelfSelectionRoute, navOptions)
}

fun NavGraphBuilder.bookshelfSelectionScreen(
    onBackClick: () -> Unit,
    onSourceClick: (BookshelfType) -> Unit
) {
    composable(BookshelfSelectionRoute) {
        BookshelfSelectionScreen(onBackClick, onSourceClick)
    }
}
