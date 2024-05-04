package com.sorrowblue.comicviewer.feature.library.dropbox.navigation

import androidx.compose.runtime.Composable
import com.ramcosta.composedestinations.annotation.ExternalModuleGraph
import com.ramcosta.composedestinations.annotation.NavGraph
import com.ramcosta.composedestinations.navigation.DependenciesContainerBuilder
import com.sorrowblue.comicviewer.feature.library.dropbox.DropBoxArgs
import com.sorrowblue.comicviewer.feature.library.dropbox.NavGraphs
import com.sorrowblue.comicviewer.feature.library.serviceloader.DropBoxNavGraph

@NavGraph<ExternalModuleGraph>
internal annotation class DropBoxGraph

internal object DropBoxNavGraphImpl : DropBoxNavGraph {

    override val navGraph get() = NavGraphs.dropBox

    override val direction get() = NavGraphs.dropBox(DropBoxArgs())

    @Composable
    override fun DependenciesContainerBuilder<*>.Dependency() {
        DropBoxGraphDependencies()
    }

    class ProviderImpl : DropBoxNavGraph.Provider {
        override fun get() = DropBoxNavGraphImpl
    }
}
