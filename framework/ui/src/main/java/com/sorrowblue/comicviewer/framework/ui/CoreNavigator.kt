package com.sorrowblue.comicviewer.framework.ui

import androidx.navigation.NavController

interface CoreNavigator {
    val navController: NavController
    fun navigateUp()
}
