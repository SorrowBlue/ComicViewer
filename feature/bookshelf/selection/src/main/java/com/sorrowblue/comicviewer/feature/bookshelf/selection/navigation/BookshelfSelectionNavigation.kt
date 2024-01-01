package com.sorrowblue.comicviewer.feature.bookshelf.selection.navigation

import androidx.navigation.NavGraphBuilder
import com.ramcosta.composedestinations.utils.composable
import com.ramcosta.composedestinations.utils.dialogComposable
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.feature.bookshelf.selection.BookshelfSelectionScreen
import com.sorrowblue.comicviewer.feature.bookshelf.selection.destinations.BookshelfSelectionScreenDestination
import com.sorrowblue.comicviewer.framework.ui.ComposeValue

context(ComposeValue)
fun NavGraphBuilder.bookshelfSelectionScreen(
    onBackClick: () -> Unit,
    onSourceClick: (BookshelfType) -> Unit,
) {
    if (isCompact) {
        composable(BookshelfSelectionScreenDestination) {
            BookshelfSelectionScreen(onBackClick, onSourceClick, contentPadding)
        }
    } else {
        dialogComposable(BookshelfSelectionScreenDestination) {
            BookshelfSelectionScreen(onBackClick, onSourceClick, contentPadding)
        }
    }
}
