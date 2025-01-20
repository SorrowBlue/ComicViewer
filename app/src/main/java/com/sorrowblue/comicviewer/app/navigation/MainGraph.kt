package com.sorrowblue.comicviewer.app.navigation

import com.ramcosta.composedestinations.annotation.ExternalDestination
import com.ramcosta.composedestinations.annotation.ExternalNavGraph
import com.ramcosta.composedestinations.annotation.NavHostGraph
import com.sorrowblue.comicviewer.feature.book.destinations.ReceiveBookScreenDestination
import com.sorrowblue.comicviewer.feature.bookshelf.navgraphs.BookshelfNavGraph
import com.sorrowblue.comicviewer.feature.library.navigation.LibraryNavGraph
import com.sorrowblue.comicviewer.feature.settings.navgraphs.SettingsNavGraph
import com.sorrowblue.comicviewer.feature.tutorial.navgraphs.TutorialNavGraph

/**
 * Main graph
 *
 * @constructor Create empty Main graph
 */
@NavHostGraph
internal annotation class MainGraph {

    @ExternalNavGraph<BookshelfNavGraph>(start = true)
    @ExternalNavGraph<SettingsNavGraph>
    @ExternalNavGraph<LibraryNavGraph>
    @ExternalNavGraph<TutorialNavGraph>
    @ExternalDestination<ReceiveBookScreenDestination>
    companion object Includes
}
