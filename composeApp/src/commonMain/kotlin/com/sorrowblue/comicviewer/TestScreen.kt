package com.sorrowblue.comicviewer

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.sorrowblue.comicviewer.feature.bookshelf.navgraph.BookshelfNavGraph
import com.sorrowblue.comicviewer.framework.annotation.NestedNavGraph
import com.sorrowblue.comicviewer.framework.navigation.NavGraph
import com.sorrowblue.comicviewer.framework.navigation.NavGraphNavHost
import com.sorrowblue.comicviewer.framework.ui.core.isCompactWindowClass
import kotlinx.serialization.Serializable

@Composable
fun App() {
    NavGraphNavHost(
        navGraph = RootNavGraphImpl(),
        isCompact = isCompactWindowClass(),
        navController = rememberNavController()
    )
}


@Serializable
data object Root

expect class RootNavGraphImpl() : NavGraph

@com.sorrowblue.comicviewer.framework.annotation.NavGraph<Root>(
    startDestination = BookshelfNavGraph::class,
    root = RootNavGraphImpl::class
)
internal class RootGraph {

    @NestedNavGraph<BookshelfNavGraph>
    companion object
}
