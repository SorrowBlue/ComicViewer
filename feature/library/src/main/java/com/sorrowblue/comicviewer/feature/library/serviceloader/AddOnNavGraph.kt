package com.sorrowblue.comicviewer.feature.library.serviceloader

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.ramcosta.composedestinations.navigation.DependenciesContainerBuilder
import com.sorrowblue.comicviewer.framework.ui.AnimatedNavGraphSpec
import java.util.ServiceLoader

interface AddOnNavigation {

    interface Provider {
        fun get(): AddOnNavigation
    }

    fun NavGraphBuilder.addOnScreen(navController: NavController)

    fun NavController.navigateToAddOnScreen()
}

sealed interface AddOnNavGraph : AnimatedNavGraphSpec {

    interface Provider {
        fun get(): AddOnNavGraph
    }

    context(DependenciesContainerBuilder<*>)
    fun dependency()
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
            BoxNavGraph.Provider::class.java,
            BoxNavGraph.Provider::class.java.classLoader
        )
    }
}

interface GoogleDriveNavGraph : AddOnNavGraph {

    interface Provider : AddOnNavGraph.Provider

    companion object {
        operator fun invoke() = loadAddOnNavGraph(
            BoxNavGraph.Provider::class.java,
            BoxNavGraph.Provider::class.java.classLoader
        )
    }
}

interface OneDriveNavGraph : AddOnNavGraph {

    interface Provider : AddOnNavGraph.Provider

    companion object {
        operator fun invoke() = loadAddOnNavGraph(
            BoxNavGraph.Provider::class.java,
            BoxNavGraph.Provider::class.java.classLoader
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
