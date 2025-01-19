package com.sorrowblue.comicviewer.feature.bookshelf.navigation

import com.ramcosta.composedestinations.annotation.ExternalDestination
import com.ramcosta.composedestinations.annotation.ExternalModuleGraph
import com.ramcosta.composedestinations.annotation.NavGraph
import com.sorrowblue.comicviewer.feature.folder.destinations.SortTypeDialogDestination

@NavGraph<ExternalModuleGraph>(defaultTransitions = BookshelfGraphTransitions::class)
internal annotation class BookshelfGraph {

    @ExternalDestination<SortTypeDialogDestination>
    companion object Includes
}
