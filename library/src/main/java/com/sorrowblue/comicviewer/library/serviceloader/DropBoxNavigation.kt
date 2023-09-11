package com.sorrowblue.comicviewer.library.serviceloader

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder

interface DropBoxNavigation {
    interface Provider {
        fun get(): DropBoxNavigation
    }

    fun NavGraphBuilder.dropBoxScreen(navController: NavController)
    fun NavController.navigateToDropBox(path: String = "")
}
