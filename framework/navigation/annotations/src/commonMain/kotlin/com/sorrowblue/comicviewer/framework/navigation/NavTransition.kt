package com.sorrowblue.comicviewer.framework.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SizeTransform
import androidx.navigation.NavBackStackEntry

abstract class NavTransition {
    abstract fun AnimatedContentTransitionScope<NavBackStackEntry>.enterTransition(): EnterTransition

    abstract fun AnimatedContentTransitionScope<NavBackStackEntry>.exitTransition(): ExitTransition

    abstract fun AnimatedContentTransitionScope<NavBackStackEntry>.popEnterTransition(): EnterTransition

    abstract fun AnimatedContentTransitionScope<NavBackStackEntry>.popExitTransition(): ExitTransition

    abstract fun AnimatedContentTransitionScope<NavBackStackEntry>.sizeTransform(): SizeTransform?

    companion object Default : NavTransition() {
        override fun AnimatedContentTransitionScope<NavBackStackEntry>.enterTransition(): EnterTransition {
            return EnterTransition.None
        }

        override fun AnimatedContentTransitionScope<NavBackStackEntry>.exitTransition(): ExitTransition {
            return ExitTransition.None
        }

        override fun AnimatedContentTransitionScope<NavBackStackEntry>.popEnterTransition(): EnterTransition {
            return enterTransition()
        }

        override fun AnimatedContentTransitionScope<NavBackStackEntry>.popExitTransition(): ExitTransition {
            return exitTransition()
        }

        override fun AnimatedContentTransitionScope<NavBackStackEntry>.sizeTransform(): SizeTransform? {
            return null
        }
    }
}
