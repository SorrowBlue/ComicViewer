package com.sorrowblue.comicviewer.library.serviceloader

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder

interface GoogleDriveNavigation {
    interface Provider {
        fun get(): GoogleDriveNavigation
    }

    fun NavGraphBuilder.googleDriveScreen(navController: NavController)
    fun NavController.navigateToGoogleDrive(path: String = "root")
}
