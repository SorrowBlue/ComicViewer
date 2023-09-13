package com.sorrowblue.comicviewer.feature.library.serviceloader

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder

interface AddOnNavigation {

    interface Provider {
        fun get(): AddOnNavigation
    }

    fun NavGraphBuilder.addOnScreen(navController: NavController)

    fun NavController.navigateToAddOnScreen()
}
