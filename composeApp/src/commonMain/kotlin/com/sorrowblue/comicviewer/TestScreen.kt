package com.sorrowblue.comicviewer

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.sorrowblue.comicviewer.feature.bookshelf.BookshelfNav
import com.sorrowblue.comicviewer.framework.annotation.NestedNavGraph
import com.sorrowblue.comicviewer.framework.navigation.NavGraph
import com.sorrowblue.comicviewer.framework.navigation.NavGraphNavHost
import kotlinx.serialization.Serializable

@Composable
fun App() {
    NavGraphNavHost<NavGraph>(
        navGraph = RootNavGraphImpl(),
        navController = rememberNavController()
    )
}


@Serializable
data object Root

expect class RootNavGraphImpl() : NavGraph

@com.sorrowblue.comicviewer.framework.annotation.NavGraph<Root>(
    startDestination = BookshelfNav::class,
    root = RootNavGraphImpl::class
)
class RootGraph {

    @NestedNavGraph<BookshelfNav>
    companion object
}
