package com.sorrowblue.comicviewer.feature.library.serviceloader

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder

interface OneDriveNavigation {
    interface Provider {
        fun get(): OneDriveNavigation
    }

    fun NavGraphBuilder.oneDriveScreen(navController: NavController)
    fun NavController.navigateToOneDrive(driveId: String? = null, itemId: String? = null)
}
