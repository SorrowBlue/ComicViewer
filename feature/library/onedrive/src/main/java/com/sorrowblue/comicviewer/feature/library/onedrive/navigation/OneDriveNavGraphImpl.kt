package com.sorrowblue.comicviewer.feature.library.onedrive.navigation

import androidx.compose.runtime.Composable
import com.ramcosta.composedestinations.annotation.ExternalModuleGraph
import com.ramcosta.composedestinations.annotation.NavGraph
import com.ramcosta.composedestinations.navigation.DependenciesContainerBuilder
import com.sorrowblue.comicviewer.feature.library.onedrive.NavGraphs
import com.sorrowblue.comicviewer.feature.library.onedrive.OneDriveArgs
import com.sorrowblue.comicviewer.feature.library.serviceloader.OneDriveNavGraph

@NavGraph<ExternalModuleGraph>
internal annotation class OneDriveGraph

internal object OneDriveNavGraphImpl : OneDriveNavGraph {

    override val navGraph get() = NavGraphs.oneDrive
    override val direction get() = NavGraphs.oneDrive(OneDriveArgs())

    @Composable
    override fun DependenciesContainerBuilder<*>.Dependency() {
        OneDriveGraphDependencies()
    }

    internal class ProviderImpl : OneDriveNavGraph.Provider {
        override fun get() = OneDriveNavGraphImpl
    }
}
