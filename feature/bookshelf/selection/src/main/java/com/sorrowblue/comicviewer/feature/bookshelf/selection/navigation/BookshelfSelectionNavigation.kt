package com.sorrowblue.comicviewer.feature.bookshelf.selection.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.feature.bookshelf.selection.BookshelfSelectionScreen

const val BookshelfSelectionRoute = "bookshelf/selection"

fun NavGraphBuilder.bookshelfSelectionScreen(
    onBackClick: () -> Unit,
    onSourceClick: (BookshelfType) -> Unit
) {
    composable(BookshelfSelectionRoute) {
        BookshelfSelectionScreen(onBackClick, onSourceClick)
    }
}
