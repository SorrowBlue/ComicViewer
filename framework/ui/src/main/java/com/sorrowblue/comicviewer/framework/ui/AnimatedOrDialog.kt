package com.sorrowblue.comicviewer.framework.ui

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.dialog
import com.ramcosta.composedestinations.manualcomposablecalls.DestinationLambda
import com.ramcosta.composedestinations.manualcomposablecalls.ManualComposableCalls
import com.ramcosta.composedestinations.manualcomposablecalls.allDeepLinks
import com.ramcosta.composedestinations.navigation.DependenciesContainerBuilder
import com.ramcosta.composedestinations.scope.DestinationScopeImpl
import com.ramcosta.composedestinations.spec.TypedDestinationSpec

abstract class AnimatedOrDialog : DestinationTransitions() {

    companion object Default : AnimatedOrDialog() {
        override val transitions = emptyList<TransitionsConfigure>()
    }

    @SuppressLint("RestrictedApi")
    fun <T> NavGraphBuilder.addDialog(
        destination: TypedDestinationSpec<T>,
        navController: NavHostController,
        dependenciesContainerBuilder: @Composable DependenciesContainerBuilder<*>.() -> Unit,
        manualComposableCalls: ManualComposableCalls,
    ) {
        @Suppress("UNCHECKED_CAST")
        val contentLambda = manualComposableCalls[destination.route] as? DestinationLambda<T>?

        dialog(
            destination.route,
            destination.arguments,
            destination.allDeepLinks(manualComposableCalls),
            DialogProperties()
        ) { navBackStackEntry ->
            CallDialogComposable(
                destination,
                navController,
                navBackStackEntry,
                contentLambda,
                dependenciesContainerBuilder
            )
        }
    }

    @SuppressLint("RestrictedApi")
    @Composable
    private fun <T> CallDialogComposable(
        destination: TypedDestinationSpec<T>,
        navController: NavHostController,
        navBackStackEntry: NavBackStackEntry,
        contentWrapper: DestinationLambda<T>?,
        content: @Composable DependenciesContainerBuilder<*>.() -> Unit,
    ) {
        val scope =
            remember(destination, navBackStackEntry, navController, content) {
                DestinationScopeImplDefault(
                    destination,
                    navBackStackEntry,
                    navController,
                    content,
                )
            }

        if (contentWrapper == null) {
            with(destination) { scope.Content() }
        } else {
            contentWrapper(scope)
        }
    }

    @SuppressLint("RestrictedApi")
    private class DestinationScopeImplDefault<T>(
        override val destination: TypedDestinationSpec<T>,
        override val navBackStackEntry: NavBackStackEntry,
        override val navController: NavController,
        override val dependenciesContainerBuilder: @Composable DependenciesContainerBuilder<*>.() -> Unit,
    ) : DestinationScopeImpl<T>()
}
