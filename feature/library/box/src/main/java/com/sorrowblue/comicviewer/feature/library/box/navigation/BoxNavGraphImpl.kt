package com.sorrowblue.comicviewer.feature.library.box.navigation

import androidx.compose.runtime.Composable
import com.ramcosta.composedestinations.annotation.ExternalModuleGraph
import com.ramcosta.composedestinations.annotation.NavGraph
import com.ramcosta.composedestinations.navigation.DependenciesContainerBuilder
import com.sorrowblue.comicviewer.feature.library.box.BoxArgs
import com.sorrowblue.comicviewer.feature.library.box.NavGraphs
import com.sorrowblue.comicviewer.feature.library.serviceloader.BoxNavGraph

@NavGraph<ExternalModuleGraph>
internal annotation class BoxGraph

internal object BoxNavGraphImpl : BoxNavGraph {

    override val navGraph get() = NavGraphs.box

    override val direction get() = NavGraphs.box(BoxArgs())

    @Composable
    override fun DependenciesContainerBuilder<*>.Dependency() {
        BoxGraphDependencies()
    }

    internal class ProviderImpl : BoxNavGraph.Provider {
        override fun get() = BoxNavGraphImpl
    }
}
