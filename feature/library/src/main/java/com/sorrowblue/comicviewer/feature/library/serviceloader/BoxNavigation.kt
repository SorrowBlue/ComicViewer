package com.sorrowblue.comicviewer.feature.library.serviceloader

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder

interface BoxNavigation {
    interface Provider {
        fun get(): BoxNavigation
    }

    fun NavGraphBuilder.boxScreen(navController: NavController)
    fun NavController.navigateToBox(path: String = "")
}
