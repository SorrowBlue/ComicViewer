package com.sorrowblue.comicviewer.feature.library.googledrive.navigation

import androidx.compose.runtime.Composable
import com.ramcosta.composedestinations.navigation.DependenciesContainerBuilder
import com.sorrowblue.comicviewer.feature.library.googledrive.GoogleDriveArgs
import com.sorrowblue.comicviewer.feature.library.googledrive.NavGraphs
import com.sorrowblue.comicviewer.feature.library.serviceloader.GoogleDriveNavGraph

internal object GoogleDriveNavGraphImpl : GoogleDriveNavGraph {

    override val navGraph get() = NavGraphs.googleDrive
    override val direction get() = NavGraphs.googleDrive(GoogleDriveArgs())

    @Composable
    override fun DependenciesContainerBuilder<*>.Dependency() {
        GoogleDriveGraphDependencies()
    }

    internal class ProviderImpl : GoogleDriveNavGraph.Provider {
        override fun get() = GoogleDriveNavGraphImpl
    }
}
