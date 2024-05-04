package com.sorrowblue.comicviewer.feature.library.serviceloader

import androidx.compose.runtime.Composable
import com.ramcosta.composedestinations.navigation.DependenciesContainerBuilder
import com.ramcosta.composedestinations.spec.Direction
import com.ramcosta.composedestinations.spec.NavGraphSpec
import java.util.ServiceLoader

sealed interface AddOnNavGraph {

    val navGraph: NavGraphSpec
    val direction: Direction

    @Composable
    fun DependenciesContainerBuilder<*>.Dependency()

    interface Provider {
        fun get(): AddOnNavGraph
    }
}

interface BoxNavGraph : AddOnNavGraph {

    interface Provider : AddOnNavGraph.Provider

    companion object {
        operator fun invoke() = loadAddOnNavGraph(
            Provider::class.java,
            Provider::class.java.classLoader
        )
    }
}

interface DropBoxNavGraph : AddOnNavGraph {

    interface Provider : AddOnNavGraph.Provider

    companion object {
        operator fun invoke() = loadAddOnNavGraph(
            Provider::class.java,
            Provider::class.java.classLoader
        )
    }
}

interface GoogleDriveNavGraph : AddOnNavGraph {

    interface Provider : AddOnNavGraph.Provider

    companion object {
        operator fun invoke() = loadAddOnNavGraph(
            Provider::class.java,
            Provider::class.java.classLoader
        )
    }
}

interface OneDriveNavGraph : AddOnNavGraph {

    interface Provider : AddOnNavGraph.Provider

    companion object {
        operator fun invoke() = loadAddOnNavGraph(
            Provider::class.java,
            Provider::class.java.classLoader
        )
    }
}

private fun <S : AddOnNavGraph.Provider> loadAddOnNavGraph(
    service: Class<S>,
    loader: ClassLoader?,
): AddOnNavGraph? {
    return runCatching {
        ServiceLoader.load(service, loader).iterator().next().get()
    }.getOrNull()
}
