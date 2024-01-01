package com.sorrowblue.comicviewer.feature.bookshelf.edit.navigation

import androidx.navigation.NavGraphBuilder
import com.ramcosta.composedestinations.utils.composable
import com.ramcosta.composedestinations.utils.dialogComposable
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.feature.bookshelf.edit.BookshelfEditScreen
import com.sorrowblue.comicviewer.feature.bookshelf.edit.destinations.BookshelfEditScreenDestination
import com.sorrowblue.comicviewer.framework.ui.ComposeValue

data class BookshelfEditArgs(
    val bookshelfId: BookshelfId = BookshelfId.Default,
    val bookshelfType: BookshelfType = BookshelfType.DEVICE,
)

context(ComposeValue)
fun NavGraphBuilder.bookshelfEditScreen(
    onBackClick: () -> Unit,
    onComplete: () -> Unit,
) {
    if (isCompact) {
        composable(BookshelfEditScreenDestination) {
            BookshelfEditScreen(
                args = navArgs,
                onBackClick = onBackClick,
                onComplete = onComplete,
                contentPadding = contentPadding
            )
        }
    } else {
        dialogComposable(BookshelfEditScreenDestination) {
            BookshelfEditScreen(
                args = navArgs,
                onBackClick = onBackClick,
                onComplete = onComplete,
                contentPadding = contentPadding
            )
        }
    }
}
