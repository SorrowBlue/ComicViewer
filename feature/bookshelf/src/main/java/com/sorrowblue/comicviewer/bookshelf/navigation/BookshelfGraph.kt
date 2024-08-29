package com.sorrowblue.comicviewer.bookshelf.navigation

import com.ramcosta.composedestinations.annotation.ExternalDestination
import com.ramcosta.composedestinations.annotation.ExternalModuleGraph
import com.ramcosta.composedestinations.annotation.NavGraph
import com.sorrowblue.comicviewer.feature.bookshelf.edit.destinations.BookshelfEditScreenDestination
import com.sorrowblue.comicviewer.feature.bookshelf.selection.destinations.BookshelfSelectionScreenDestination
import com.sorrowblue.comicviewer.feature.folder.destinations.SortTypeDialogDestination

@NavGraph<ExternalModuleGraph>(defaultTransitions = BookshelfGraphTransitions::class)
internal annotation class BookshelfGraph {

    @ExternalDestination<BookshelfSelectionScreenDestination>(style = BookshelfGraphTransitionsWithDialog::class)
    @ExternalDestination<BookshelfEditScreenDestination>(style = BookshelfGraphTransitionsWithDialog::class)
    @ExternalDestination<SortTypeDialogDestination>
    companion object Includes
}
