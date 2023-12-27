package com.sorrowblue.comicviewer.feature.bookshelf.selection.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.feature.bookshelf.selection.BookshelfSelectionScreen
import com.sorrowblue.comicviewer.framework.ui.ComposeValue

const val BookshelfSelectionRoute = "bookshelf/selection"

context(ComposeValue)
@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.bookshelfSelectionScreen(
    onBackClick: () -> Unit,
    onSourceClick: (BookshelfType) -> Unit,
) {
    if (isCompact) {
        composable(BookshelfSelectionRoute) {
            BookshelfSelectionScreen(onBackClick, onSourceClick, contentPadding)
        }
    } else {
        dialog(BookshelfSelectionRoute) {
            BookshelfSelectionScreen(onBackClick, onSourceClick, contentPadding)
        }
    }
}

fun NavController.navigateToBookshelfSelection(navOptions: NavOptions? = null) {
    navigate(BookshelfSelectionRoute, navOptions)
}
